package cc.ayakurayuki.repo.singleflight;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ayakura Yuki
 * @date 2024/01/16-11:14
 */
@RunWith(JUnit4.class)
public class TestSingleflight {

  @Test
  public void demo() throws InterruptedException {
    Singleflight singleflightSingleflight = new Singleflight();

    int taskAmount = 100;
    String key = "singleflight:task:group";

    CountDownLatch countDownLatch = new CountDownLatch(taskAmount);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);

    // demo start
    for (int i = 0; i < taskAmount; i++) {

      executor.submit(() -> {

        try {

          String result = singleflightSingleflight.run(key, () -> {

            System.out.println("simulating an IO operate");

            try {
              Thread.sleep(200L);
            } catch (InterruptedException ignored) {
            }

            return "done";

          });

          System.out.println(result);

        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        countDownLatch.countDown();

      });

    }
    // demo end

    countDownLatch.await();
    executor.shutdown();
  }

  @Test
  public void simulateRequest() throws InterruptedException {
    Singleflight singleflightSingleflight = new Singleflight();
    int taskAmount = 1000;
    ExecutorService executorService = Executors.newWorkStealingPool((int) (Runtime.getRuntime().availableProcessors() / 0.1 * 2));

    CountDownLatch a = new CountDownLatch(taskAmount);
    for (int i = 0; i < taskAmount; i++) {
      executorService.submit(() -> {
        try {
          String result = singleflightSingleflight.run("singleflight:simulate:requests:1", () -> {
            System.out.println("simulating an IO operate in request group 1");
            try {
              Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
            return "done";
          });
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        a.countDown();
      });
    }

    CountDownLatch b = new CountDownLatch(taskAmount);
    for (int i = 0; i < taskAmount; i++) {
      executorService.submit(() -> {
        try {
          String result = singleflightSingleflight.run("singleflight:simulate:requests:2", () -> {
            System.out.println("simulating an IO operate in request group 2");
            try {
              Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
            return "done";
          });
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        b.countDown();
      });
    }

    a.await();
    b.await();
    executorService.shutdown();
  }

}
