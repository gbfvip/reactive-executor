package reactive.excutor;

import reactive.internal.ReactiveFutureTask;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/10-16:12.
 * this self escape executor use ScheduledThreadPool to implement this feature
 * since ScheduledThreadPool uses delay queue to keep delay tasks in orders,
 * this makes every enqueue/dequeue cost O(log n) due to binary search for tasks shift up/shift down
 * not suitable for add/cancel tasks frequently
 * <p/>
 */
public class EscapableExecutor extends AbstractReactiveExecutor {
    /**
     * use given executor for tasks executor,suitable for most cases
     * use ScheduledThreadPool and single thread for self escape watcher
     */
    public EscapableExecutor(ExecutorService executor) {
        underling = executor;
        doorkeeper = Executors.newScheduledThreadPool(1);
    }

    /**
     * use CachedThreadPool for tasks executor,only suitable for short-lived tasks(none IO)
     * use ScheduledThreadPool and single thread for self escape watcher
     */
    public EscapableExecutor() {
        underling = Executors.newCachedThreadPool();
        doorkeeper = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void shutdown() {
        doorkeeper.shutdown();
        underling.shutdown();
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
        underling.execute(command);
    }

    @Override
    @Deprecated
    public ReactiveFutureTask<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    @Deprecated
    public <T> ReactiveFutureTask<T> submit(Runnable task, T resultValue) {
        return super.submit(task, resultValue);
    }

    @Override
    @Deprecated
    public <T> ReactiveFutureTask<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    public ReactiveFutureTask<?> submit(Runnable task, long delay, TimeUnit timeUnit) {
        final ReactiveFutureTask<?> future = super.submit(task);
        initializeWatcher(future, delay, timeUnit);
        return future;
    }

    public <T> ReactiveFutureTask<T> submit(Runnable task, T resultValue, long delay, TimeUnit timeUnit) {
        final ReactiveFutureTask<T> future = super.submit(task, resultValue);
        initializeWatcher(future, delay, timeUnit);
        return future;
    }

    public <T> ReactiveFutureTask<T> submit(Callable<T> task, long delay, TimeUnit timeUnit) {
        final ReactiveFutureTask<T> future = super.submit(task);
        initializeWatcher(future, delay, timeUnit);
        return future;
    }
}
