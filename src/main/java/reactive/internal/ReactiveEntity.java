package reactive.internal;

import reactive.ReactiveTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by GaoBinfang on 2016/11/11-15:38.
 * post action for future task
 * note that here I use spinning to avoid RejectedExecutionException when reactive tasks are submitted
 * this maybe a performance lose in some cases,but no better way than this to make sure reactive happens right now
 */
public class ReactiveEntity<T> implements TaskExecutorPair<T> {
    private static final Logger log = Logger.getLogger(ReactiveEntity.class.getName());
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
                log.log(Level.SEVERE, "Exception while raising cancellation event to executor " + executor, e);
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
                log.log(Level.SEVERE, "Exception while raising success event to executor " + executor, e);
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
                log.log(Level.SEVERE, "Exception while raising exception event to executor " + executor, e);
                break;
            }
        }
    }
}
