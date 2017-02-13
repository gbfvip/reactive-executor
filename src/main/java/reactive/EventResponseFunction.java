package reactive;

/**
 * Created by GaoBinfang on 2017/2/10-18:27.
 * in order to split different event handle into different entity,here EventResponseFunction represent a specific handle
 * NOTE that parameter should not be used when use it to implement onCancellation
 */
public interface EventResponseFunction<T> {

    /**
     * parameter will always be null when implement onCancellation
     *
     * @throws java.lang.NullPointerException if parameter is used when implement Cancellation.
     */
    public void onEventRaise(T luggage);
}
