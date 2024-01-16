# singleflight

Singleflight is a simple tool transplanted from `golang.org/x/sync/singleflight`.

## Usage

```java
import io.github.ayakurayuki.singleflight.Group;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {

  public static void main(String[] args) {
    Group singleflightGroup = new Group();

    int taskAmount = 100;
    String key = "singleflight:task:group";

    CountDownLatch countDownLatch = new CountDownLatch(taskAmount);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);

    for (int i = 0; i < taskAmount; i++) {

      executor.submit(() -> {

        try {

          // run task with singleflight group
          String result = singleflightGroup.run(key, () -> {

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

    countDownLatch.await();
    executor.shutdown();
  }

}
```
