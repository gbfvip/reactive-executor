package reactive.newescape;

import reactive.excutor.AbstractReactiveExecutor;
import reactive.internal.Callback;
import reactive.internal.ReactiveFutureTask;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by GaoBinfang on 2016/12/16-11:26.
 */
public class TimeWheelExecutor extends AbstractReactiveExecutor {
    private ScheduledExecutorService underling = Executors.newScheduledThreadPool(1);
    private long interval;

    public TimeWheelExecutor(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
        underling.submit(new Worker());
    }

    @Override
    public void shutdown() {
        underling.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
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

    @Deprecated
    @Override
    public ReactiveFutureTask<?> submit(Runnable task) {
        return null;
    }

    @Deprecated
    @Override
    public <T> ReactiveFutureTask<T> submit(Runnable task, T resultValue) {
        return null;
    }

    @Deprecated
    @Override
    public <T> ReactiveFutureTask<T> submit(Callable<T> task) {
        return null;
    }

    public void schedule(EscapableFutureTask<?> future, long delay, TimeUnit unit) {
        future.setTimeout(delay, unit);
        TimeWheelHolder.TIME_WHEEL.putConcurrentFence();
        try {
            TimeWheelHolder.TIME_WHEEL.registry(future);
        } finally {
            TimeWheelHolder.TIME_WHEEL.releaseConcurrentFence();
        }
    }

    class Worker implements Runnable {
        @Override
        public void run() {
            try {
                final long now = System.currentTimeMillis();
                TimeWheelHolder.TIME_WHEEL.putExclusiveFence();
                try {
                    TimeWheelHolder.TIME_WHEEL.map(new Callback<EscapableFutureTask<?>>() {
                        @Override
                        public void callback(EscapableFutureTask<?> item) {
                            if (item.isDone()) {
                                item.cancel(true);
                            } else if (now >= (item.getTimeout() - interval)) {
                                item.cancel(true);
                            }
                        }
                    });
                } finally {
                    TimeWheelHolder.TIME_WHEEL.releaseExclusiveFence();
                }
            } finally {
                underling.schedule(this, interval, TimeUnit.MILLISECONDS);
            }
        }
    }
}
