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
     * 使用默认FixedThreadPool(1)作为事件相应执行器,只适合响应操作类型为短平快的场景
     */
    public ReactiveEntity(ReactiveTask reactiveTask) {
        realWork = reactiveTask;
        this.executor = DEFAULT_REACTIVE_HANDLER;
    }

    /**
     * 使用给定的executor作为事件相应执行器,性能取决于传入的executor,适合大多数响应场景
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
