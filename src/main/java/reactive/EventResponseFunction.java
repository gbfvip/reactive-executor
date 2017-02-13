package reactive;

/**
 * Created by GaoBinfang on 2017/2/10-18:27.
 */
public interface EventResponseFunction<T> {
    public void onEventRaise(T luggage);
}
