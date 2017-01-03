package reactive.internal;

/**
 * Created by GaoBinfang on 2016/12/16-14:07.
 */
public interface Callback<T> {
    /**
     * common callback
     */
    public void callback(T item);
}
