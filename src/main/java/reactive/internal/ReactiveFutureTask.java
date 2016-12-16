package reactive.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by GaoBinfang on 2016/11/11-14:52.
 */
public class ReactiveFutureTask<T> extends FutureTask<T> {
    private List<TaskExecutorPair<T>> reactiveList = new ArrayList<>();
    private AtomicBoolean done = new AtomicBoolean(false);
    private ReentrantLock reactFence = new ReentrantLock();

    public ReactiveFutureTask(Callable<T> callable) {
        super(callable);
    }

    public ReactiveFutureTask(Runnable runnable, T resultValue) {
        super(runnable, resultValue);
    }

    /**
     * 在宿主任务上添加事件相应机制
     */
    public void addReactEvent(TaskExecutorPair<T> pair) {
        reactFence.lock();
        //加锁成功之后,说明此future还没有开始执行回调列表,或者回调已经执行完毕
        try {
            if (done.get()) {
                executeBasedOnCondition(pair);
            } else {
                reactiveList.add(pair);
            }
        } finally {
            reactFence.unlock();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        reactFence.lock();
        try {
            if (!done.get()) {
                return super.cancel(mayInterruptIfRunning);
            }
        } finally {
            reactFence.unlock();
        }
        return false;
    }

    @Override
    protected void done() {
        //此标志位位于lock之外,用于确保添加回调的线程在进入lock之后,future只能是回调执行完毕状态或者还未执行状态
        done.set(true);
        reactFence.lock();
        try {
            Iterator<TaskExecutorPair<T>> iterator = reactiveList.iterator();
            //开始执行一个回调,就删掉一个,以利于回调任务实例完成之后快速回收
            while (iterator.hasNext()) {
                TaskExecutorPair<T> pair = iterator.next();
                iterator.remove();
                executeBasedOnCondition(pair);
            }
        } finally {
            reactFence.unlock();
        }
    }

    private void executeBasedOnCondition(TaskExecutorPair<T> pair) {
        if (super.isCancelled()) {
            pair.executeOnCancellation();
        } else {
            try {
                T result = super.get();
                pair.executeOnSuccess(result);
            } catch (Throwable e) {
                pair.executeOnException(e);
            }
        }
    }
}
