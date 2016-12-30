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
     * 使用readLock特性,当没有Worker在扫描整个map时,可以并行地放任务
     */
    void putConcurrentFence() {
        readFence.lock();
    }

    /**
     * 使用readLock特性,当没有Worker在扫描整个map时,可以并行地放任务
     */
    void releaseConcurrentFence() {
        readFence.unlock();
    }

    /**
     * 使用writeLock特性,当有Worker在扫描整个map时,停止放任务
     */
    void putExclusiveFence() {
        writeFence.lock();
    }

    /**
     * 使用writeLock特性,当有Worker在扫描整个map时,停止放任务
     */
    void releaseExclusiveFence() {
        writeFence.unlock();
    }
}