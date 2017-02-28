package reactive.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by GaoBinfang on 2017/2/28-17:22.
 */
public enum EventHandler {
    INSTANCE;
    private ThreadPoolExecutor delegate;

    private EventHandler() {
        delegate = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        delegate.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    ExecutorService getDefault() {
        return this.delegate;
    }
}
