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
     * add react event on future tasks
     * since any thread should operate react pair while holding reactFence lock,
     * successfully lock means either events are raised already(need raise event when add pair) or events keep stay still(just add,no further process needed)
     */
    public void appendReactEvent(TaskExecutorPair<T> pair) {
        reactFence.lock();
        try {
            if (done.get()) {
                raiseEvent(pair);
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
        //set done before holding lock,to make sure threads doing add pair will only see two condition: callback pairs already raised,callback pairs not yet raised at all
        done.set(true);
        reactFence.lock();
        //since we have spinning in executeOnXXX(may cost some time)
        //innerList here is suppose to narrow down lock time
        //because as a executor,fast submit is a must,we don't want submit/cancel have block issue
        List<TaskExecutorPair<T>> innerList;
        try {
            innerList = reactiveList;
            reactiveList = null;
        } finally {
            reactFence.unlock();
        }
        if (innerList != null) {
            Iterator<TaskExecutorPair<T>> iterator = innerList.iterator();
            while (iterator.hasNext()) {
                TaskExecutorPair<T> pair = iterator.next();
                iterator.remove();//help GC,we don't want task already finished,but "innerList" still hold reference
                raiseEvent(pair);
            }
        }
    }

    private void raiseEvent(TaskExecutorPair<T> pair) {
        if (super.isCancelled()) {
            pair.raiseCancellationEvent();
        } else {
            try {
                T result = super.get();
                pair.raiseSuccessEvent(result);
            } catch (Throwable e) {
                pair.raiseExceptionEvent(e);
            }
        }
    }
}
