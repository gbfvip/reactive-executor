package reactive.newescape;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/10-16:12.
 * 使用ScheduledExecutorService时,还有一个额外包装,应想办法避免
 * <p/>
 */
public class EscapableExecutor extends AbstractEscapableReactiveExecutor {

    /**
     * 包装给定的executor,适合大多数响应场景并且可以设置超时相应事件的容忍度
     */
    public EscapableExecutor(ExecutorService executor, long delay, TimeUnit unit) {
        underling = executor;
        doorkeeper = new TimeWheelExecutor(delay, unit);
    }

    /**
     * 包装给定的executor,适合大多数响应场景
     */
    public EscapableExecutor(ExecutorService executor) {
        underling = executor;
        doorkeeper = new TimeWheelExecutor(100, TimeUnit.MILLISECONDS);
    }

    /**
     * 使用默认CachedThreadPool作为执行器,只适合操作类型为短平快的场景
     */
    public EscapableExecutor() {
        underling = Executors.newCachedThreadPool();
        doorkeeper = new TimeWheelExecutor(100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        underling.shutdown();
        doorkeeper.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        doorkeeper.shutdownNow();
        return underling.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return underling.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return underling.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return underling.awaitTermination(timeout, unit);
    }

    @Deprecated
    @Override
    public void execute(Runnable command) {
    }

    @Override
    @Deprecated
    public EscapableFutureTask<?> submit(Runnable task) {
        return null;
    }

    @Override
    @Deprecated
    public <T> EscapableFutureTask<T> submit(Runnable task, T resultValue) {
        return null;
    }

    @Override
    @Deprecated
    public <T> EscapableFutureTask<T> submit(Callable<T> task) {
        return null;
    }

    public EscapableFutureTask<?> submit(Runnable task, long delay, TimeUnit unit) {
        EscapableFutureTask<?> future = super.submit(task);
        doorkeeper.schedule(future, delay, unit);
        return future;
    }

    public <T> EscapableFutureTask<T> submit(Runnable task, T resultValue, long delay, TimeUnit unit) {
        EscapableFutureTask<T> future = super.submit(task, resultValue);
        doorkeeper.schedule(future, delay, unit);
        return future;
    }

    public <T> EscapableFutureTask<T> submit(Callable<T> task, long delay, TimeUnit unit) {
        EscapableFutureTask<T> future = super.submit(task);
        doorkeeper.schedule(future, delay, unit);
        return future;
    }
}
