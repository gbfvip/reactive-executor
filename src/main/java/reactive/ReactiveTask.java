package reactive;

/**
 * Created by GaoBinfang on 2016/11/11-15:44.
 */
public interface ReactiveTask<T> {
    /**
     * executed after host task successfully finished
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void onSuccess(T result);

    /**
     * executed after host task throws exception
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void onException(Throwable e);

    /**
     * executed after host task interrupted
     * note that this method will executed probably by another thread(depends on which react executor you use)
     */
    public void onCancellation();
}
