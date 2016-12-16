package reactive.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GaoBinfang on 2016/11/11-15:00.
 */
public interface TaskExecutorPair<T> {
    public static final ExecutorService DEFAULT_REACTIVE_HANDLER = Executors.newFixedThreadPool(1);

    /**
     * 在宿主任务被取消时执行,于另一个线程
     */
    public void executeOnCancellation();

    /**
     * 在宿主任务成功执行完毕后执行,于另一个线程
     */
    public void executeOnSuccess(T result);

    /**
     * 在宿主任务执行过程中出错后执行,于另一个线程
     */
    public void executeOnException(Throwable e);
}
