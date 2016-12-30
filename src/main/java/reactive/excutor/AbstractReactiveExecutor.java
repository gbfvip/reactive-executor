package reactive.excutor;

import reactive.ReactiveTask;
import reactive.internal.ReactiveEntity;
import reactive.internal.ReactiveFutureTask;

import java.util.concurrent.*;

/**
 * Created by GaoBinfang on 2016/11/11-14:39.
 * abstraction of reactive executor,using normal executor as underling to implement basic behaviors(submit,terminate,etc.)
 * only difference is construct ReactiveFutureTask when calling newTaskFor
 */
public abstract class AbstractReactiveExecutor extends AbstractExecutorService {
    protected ExecutorService minion;
    protected ScheduledExecutorService doorkeeper;

    @Override
    protected <T> ReactiveFutureTask<T> newTaskFor(Runnable runnable, T resultValue) {
        return new ReactiveFutureTask<>(runnable, resultValue);
    }

    @Override
    protected final <T> ReactiveFutureTask<T> newTaskFor(Callable<T> callable) {
        return new ReactiveFutureTask<>(callable);
    }

    @Override
    public ReactiveFutureTask<?> submit(Runnable task) {
        return (ReactiveFutureTask<?>) super.submit(task);
    }

    @Override
    public <T> ReactiveFutureTask<T> submit(Runnable task, T resultValue) {
        return (ReactiveFutureTask<T>) super.submit(task, resultValue);
    }

    @Override
    public <T> ReactiveFutureTask<T> submit(Callable<T> task) {
        return (ReactiveFutureTask<T>) super.submit(task);
    }

    protected <T> void initializeWatcher(ReactiveFutureTask<T> future, long delay, TimeUnit timeUnit) {
        Worker work = new Worker(future);
        final ScheduledFuture scheduledFuture = doorkeeper.schedule(work, delay, timeUnit);
        ReactiveEntity cancelWatcher = new ReactiveEntity(new ReactiveTask<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                scheduledFuture.cancel(true);
            }

            @Override
            public void onException(Throwable e) {
                scheduledFuture.cancel(true);
            }

            @Override
            public void onCancellation() {
                scheduledFuture.cancel(true);
            }
        });
        future.addReactEvent(cancelWatcher);
    }

    class Worker implements Runnable {
        private ReactiveFutureTask<?> future;

        Worker(ReactiveFutureTask<?> future) {
            this.future = future;
        }

        @Override
        public void run() {
            future.cancel(true);
        }
    }
}
