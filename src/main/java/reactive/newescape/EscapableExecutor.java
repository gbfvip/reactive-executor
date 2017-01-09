package reactive.newescape;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/10-16:12.
 * this self escape executor use custom hash time wheel to implement this feature
 * hash time wheel doesn't care task order,
 * this makes every enqueue/dequeue cost constance O(1),but takes a dedicated thread scan this wheel every given delay
 * this is a trade-off for such cases: matters throughput more,cost is the possibility of STW(no enqueue/dequeue allowed when scan this wheel)
 * <p/>
 */
public class EscapableExecutor extends AbstractEscapableReactiveExecutor {

    /**
     * construct EscapableExecutor using given delay
     */
    public EscapableExecutor(ExecutorService executor, long delay, TimeUnit unit) {
        underling = executor;
        doorkeeper = new TimeWheelExecutor(delay, unit);
    }

    /**
     * construct EscapableExecutor using default delay(100ms)
     */
    public EscapableExecutor(ExecutorService executor) {
        underling = executor;
        doorkeeper = new TimeWheelExecutor(100, TimeUnit.MILLISECONDS);
    }

    /**
     * construct EscapableExecutor using default delay(100ms)
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
        underling.execute(command);
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
