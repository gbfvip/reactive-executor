package reactive.newescape;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created by GaoBinfang on 2016/11/11-14:39.
 */
public abstract class AbstractEscapableReactiveExecutor extends AbstractExecutorService {
    protected ExecutorService underling;
    protected TimeWheelExecutor doorkeeper;

    @Override
    protected <T> EscapableFutureTask<T> newTaskFor(Runnable runnable, T resultValue) {
        return new EscapableFutureTask<>(runnable, resultValue);
    }

    @Override
    protected final <T> EscapableFutureTask<T> newTaskFor(Callable<T> callable) {
        return new EscapableFutureTask<>(callable);
    }

    @Override
    public EscapableFutureTask<?> submit(Runnable task) {
        return (EscapableFutureTask<?>) super.submit(task);
    }

    @Override
    public <T> EscapableFutureTask<T> submit(Runnable task, T resultValue) {
        return (EscapableFutureTask<T>) super.submit(task, resultValue);
    }

    @Override
    public <T> EscapableFutureTask<T> submit(Callable<T> task) {
        return (EscapableFutureTask<T>) super.submit(task);
    }
}
