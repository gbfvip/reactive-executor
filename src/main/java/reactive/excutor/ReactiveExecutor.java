package reactive.excutor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/11/11-16:31.
 */
public class ReactiveExecutor extends AbstractReactiveExecutor {
    /**
     * 包装给定的executor,适合大多数响应场景
     */
    public ReactiveExecutor(ExecutorService executor) {
        minion = executor;
    }

    /**
     * 使用默认CachedThreadPool作为执行器,只适合操作类型为短平快的场景
     */
    public ReactiveExecutor() {
        minion = Executors.newCachedThreadPool();
    }

    @Override
    public void shutdown() {
        minion.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return minion.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return minion.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return minion.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return minion.awaitTermination(timeout, unit);
    }

    /**
     * 因为是直接执行任务,不返回任何future对象,在扩展executor中不推荐使用此方法
     *
     * @param command the runnable task
     * @throws java.util.concurrent.RejectedExecutionException if this task cannot be
     *                                                         accepted for execution.
     * @throws NullPointerException                            if command is null
     */
    @Deprecated
    @Override
    public void execute(Runnable command) {
        minion.execute(command);
    }
}
