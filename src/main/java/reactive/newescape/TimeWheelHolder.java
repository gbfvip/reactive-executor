package reactive.newescape;

import reactive.internal.Callback;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by GaoBinfang on 2016/12/16-11:04.
 */
public enum TimeWheelHolder {
    TIME_WHEEL;

    private ConcurrentHashMap<EscapableFutureTask<?>, Object> underling;
    private static final String DUMMY = "";
    private ReentrantReadWriteLock timeWheelFence = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readFence = timeWheelFence.readLock();
    private ReentrantReadWriteLock.WriteLock writeFence = timeWheelFence.writeLock();

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
        Iterator<Map.Entry<EscapableFutureTask<?>, Object>> iterator = underling.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EscapableFutureTask<?>, Object> entry = iterator.next();
            callback.callback(entry.getKey());
        }
    }

    /**
     * allow multiple readers concurrent put task into wheel
     */
    void putConcurrentFence() {
        readFence.lock();
    }

    /**
     * allow multiple readers concurrent put task into wheel
     */
    void releaseConcurrentFence() {
        readFence.unlock();
    }

    /**
     * make sure only one writer scan wheel,and no reader operate during this interval
     */
    void putExclusiveFence() {
        writeFence.lock();
    }

    /**
     * make sure only one writer scan wheel,and no reader operate during this interval
     */
    void releaseExclusiveFence() {
        writeFence.unlock();
    }
}