package reactive.internal;

import reactive.ReactiveTask;

import java.util.concurrent.ExecutorService;

/**
 * Created by GaoBinfang on 2016/11/11-15:38.
 */
public class ReactiveEntity<T> implements TaskExecutorPair<T> {
    private ReactiveTask<T> realWork;
    private ExecutorService executor;
    private T result;
    private Throwable e;

    /**
     * using FixedThreadPool as react event handler,suitable for short-live event
     */
    public ReactiveEntity(ReactiveTask reactiveTask) {
        realWork = reactiveTask;
        this.executor = DEFAULT_REACTIVE_HANDLER;
    }

    /**
     * using given executor as react event handler,suitable for most cases
     */
    public ReactiveEntity(ReactiveTask reactiveTask, ExecutorService executor) {
        realWork = reactiveTask;
        this.executor = executor;
    }

    @Override
    public void executeOnCancellation() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                realWork.onCancellation();
            }
        });
    }

    @Override
    public void executeOnSuccess(T parameter) {
        this.result = parameter;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                realWork.onSuccess(result);
            }
        });
    }

    @Override
    public void executeOnException(Throwable throwable) {
        this.e = throwable;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                realWork.onException(e);
            }
        });
    }
}
