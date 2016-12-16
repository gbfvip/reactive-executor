package reactive;

/**
 * Created by GaoBinfang on 2016/11/11-15:44.
 */
public interface ReactiveTask<T> {
    /**
     * 在宿主任务成功执行完毕后执行,于另一个线程
     */
    public void onSuccess(T result);

    /**
     * 在宿主任务执行过程中出错后执行,于另一个线程
     */
    public void onException(Throwable e);

    /**
     * 在宿主任务被取消时执行,于另一个线程
     */
    public void onCancellation();
}
