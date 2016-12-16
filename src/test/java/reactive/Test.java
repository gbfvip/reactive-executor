package reactive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by GaoBinfang on 2016/12/16-13:39.
 */
public class Test {
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        Future future = test.executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("continue do");
                    try {
                        Thread.sleep(TimeUnit.HOURS.toMillis(1));
                    } catch (Exception e) {
                        System.out.println("receive signal,ignore");
                    }
                }
            }
        });
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("about to cancel");
        test.executorService.shutdownNow();
        test.executorService.awaitTermination(1,TimeUnit.DAYS);
    }
}
