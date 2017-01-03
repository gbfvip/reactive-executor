package reactive.excutor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/11/11-16:31.
 * ReactiveExecutor allows you using react-coding-stlye onwards submitted tasks
 * add react behaviors into ReactiveFutureTask,then ReactiveExecutor can transfer this ReactiveFutureTask's react behaviors to executor when the time is right
 */
public class ReactiveExecutor extends AbstractReactiveExecutor {
    /**
     * construct ReactiveExecutor using ordinary executor,
     * store ordinary executor as underling,and this underling will take responsibility to execute submitted tasks
     */
    public ReactiveExecutor(ExecutorService executor) {
        minion = executor;
    }

    /**
     * construct ReactiveExecutor using default CachedThreadPool,
     * this CachedThreadPool will take responsibility to execute submitted tasks
     * note that number of threads in CachedThreadPool is not limited,
     * CachedThreadPool will create new thread as long as it thinks necessary,
     * this will cause serious performance issue when submit tasks are not short-time tasks
     * because THREADS itself also consume resources
     * only RAM-style tasks are recommend for this ReactiveExecutor
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
     * this method dose not return any kind of future,so deprecated for public use
     * only for internal use
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
