package reactive.internal;

/**
 * Created by GaoBinfang on 2016/11/11-15:00.
 */
public interface TaskExecutorPair<T> {
    /**
     * executed after host task throws exception
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void raiseCancellationEvent();

    /**
     * executed after host task successfully finished
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void raiseSuccessEvent(T result);

    /**
     * executed after host task interrupted
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void raiseExceptionEvent(Throwable e);
}
