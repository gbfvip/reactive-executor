package reactive.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactive.ReactiveTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by GaoBinfang on 2016/11/11-15:38.
 * post action for future task
 * note that here I use spinning to avoid RejectedExecutionException when reactive tasks are submitted
 * this maybe a performance lose in some cases,but no better way than this to make sure reactive happens right now
 */
public class ReactiveEntity<T> implements TaskExecutorPair<T> {
    private static final Logger log = LoggerFactory.getLogger(ReactiveEntity.class.getName());
    private ReactiveTask<T> realWork;
    private ExecutorService executor;
    private T result;
    private Throwable e;

    /**
     * when using FixedThreadPool as react event handler,suitable for short-live event
     * when using given executor as react event handler,suitable for most cases
     */
    public ReactiveEntity(ReactiveTask reactiveTask, ExecutorService... executor) {
        realWork = reactiveTask;
        if (executor.length > 0) {
            this.executor = executor[0];
        } else {
            this.executor = EventHandler.INSTANCE.getDefault();
        }
    }

    @Override
    public void raiseCancellationEvent() {
        //simply use spinning to handle RejectedExecutionException,maybe a better way in the future
        for (; ; ) {
            try {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        realWork.onCancellation();
                    }
                });
                break;
            } catch (RejectedExecutionException e) {
            } catch (Throwable t) {
                log.error("Exception while raising cancellation event to executor " + executor, e);
                break;
            }
        }
    }

    @Override
    public void raiseSuccessEvent(T parameter) {
        this.result = parameter;
        //simply use spinning to handle RejectedExecutionException,maybe a better way in the future
        for (; ; ) {
            try {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        realWork.onSuccess(result);
                    }
                });
                break;
            } catch (RejectedExecutionException e) {
            } catch (Throwable t) {
                log.error("Exception while raising success event to executor " + executor, e);
                break;
            }
        }

    }

    @Override
    public void raiseExceptionEvent(Throwable throwable) {
        this.e = throwable;
        //simply use spinning to handle RejectedExecutionException,maybe a better way in the future
        for (; ; ) {
            try {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        realWork.onException(e);
                    }
                });
                break;
            } catch (RejectedExecutionException e) {
            } catch (Throwable t) {
                log.error("Exception while raising exception event to executor " + executor, e);
                break;
            }
        }
    }

    @Override
    public void checkExecutorPolicy() {
        try {
            ThreadPoolExecutor cast = (ThreadPoolExecutor) executor;
            if (!(cast.getRejectedExecutionHandler() instanceof ThreadPoolExecutor.CallerRunsPolicy) && cast.getQueue().remainingCapacity() != Integer.MAX_VALUE) {
                log.warn("executor with non CallerRunsPolicy may have performance concern with capacity limited task queue,please check!");
            }
        } catch (ClassCastException e) {
        }
    }
}
