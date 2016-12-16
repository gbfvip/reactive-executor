package reactive.newescape;

import reactive.internal.Callback;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GaoBinfang on 2016/12/16-11:04.
 */
public enum TimeWheelHolder {
    TIME_WHEEL;

    private ConcurrentHashMap<EscapableFutureTask<?>, Object> underling;
    private static final String DUMMY = "";

    TimeWheelHolder() {
        underling = new ConcurrentHashMap<>();
    }

    void registry(EscapableFutureTask<?> future) {
        underling.put(future, DUMMY);
    }

    void deRegistry(EscapableFutureTask<?> future) {
        underling.remove(future);
    }

    void map(Callback<EscapableFutureTask<?>> callback) {
        for (EscapableFutureTask<?> escapableFutureTask : underling.keySet()) {
            callback.callback(escapableFutureTask);
        }
    }
}