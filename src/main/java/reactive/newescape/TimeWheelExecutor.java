package reactive.newescape;

import reactive.internal.Callback;
import reactive.internal.EventHandler;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/16-11:26.
 */
public class TimeWheelExecutor {
    private ScheduledExecutorService underling = EventHandler.INSTANCE.getScheduledReactive();
    private long interval;

    public TimeWheelExecutor(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
        underling.submit(new Worker());
    }

    public void shutdown() {
        underling.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return underling.shutdownNow();
    }

    public boolean isShutdown() {
        return underling.isShutdown();
    }

    public boolean isTerminated() {
        return underling.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return underling.awaitTermination(timeout, unit);
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
