package reactive.newescape;

import reactive.internal.ReactiveFutureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/16-10:59.
 */
public class EscapableFutureTask<T> extends ReactiveFutureTask<T> {
    private long timeout = 0;

    public EscapableFutureTask(Callable<T> callable) {
        super(callable);
    }

    public EscapableFutureTask(Runnable runnable, T resultValue) {
        super(runnable, resultValue);
    }

    void setTimeout(long timeout, TimeUnit unit) {
        this.timeout = System.currentTimeMillis() + unit.toMillis(timeout);
    }

    long getTimeout() {
        return timeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        TimeWheelHolder.TIME_WHEEL.deRegistry(this);
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected void done() {
        TimeWheelHolder.TIME_WHEEL.deRegistry(this);
        super.done();
    }
}
