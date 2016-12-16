package reactive.excutor;

import reactive.internal.ReactiveFutureTask;

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
public class EscapableExecutor extends AbstractReactiveExecutor {
    /**
     * 包装给定的executor,适合大多数响应场景
     */
    public EscapableExecutor(ExecutorService executor) {
        minion = executor;
        doorkeeper = Executors.newScheduledThreadPool(1);
    }

    /**
     * 使用默认CachedThreadPool作为执行器,只适合操作类型为短平快的场景
     */
    public EscapableExecutor() {
        minion = Executors.newCachedThreadPool();
        doorkeeper = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void shutdown() {
        doorkeeper.shutdown();
        minion.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        doorkeeper.shutdownNow();
        return minion.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return minion.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return minion.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return minion.awaitTermination(timeout, unit);
    }

    @Deprecated
    @Override
    public void execute(Runnable command) {
        minion.execute(command);
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
