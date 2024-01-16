# singleflight

Singleflight is a simple tool transplanted from `golang.org/x/sync/singleflight`. It provides a duplicate function call suppression mechanism.

## Usage

```java
import io.github.ayakurayuki.singleflight.Singleflight;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {

  public static void main(String[] args) {
    Singleflight singleflightSingleflight = new Singleflight();

    int taskAmount = 100;
    String key = "singleflight:task:group";

    CountDownLatch countDownLatch = new CountDownLatch(taskAmount);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);

    for (int i = 0; i < taskAmount; i++) {

      executor.submit(() -> {

        try {

          // run task with singleflight group
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

    countDownLatch.await();
    executor.shutdown();
  }

}
```

### Example In SpringBoot

```java
// file: SingleflightConfig.java
@Configuration
public class SingleflightConfig {

  @Bean
  public Singleflight singleflightGroup() {
    return new Singleflight();
  }

}

// file: ApiController.java
@RestController
public class ApiController {

  @Autowired
  private Singleflight singleflightGroup;

  @PostMapping("/api")
  public String api() {
    return singleflightGroup.run("api_request", () -> {
      System.out.println("do some io operate");
      try {
        Thread.sleep(200L);
      } catch (Exception ignored){}
      return "done";
    });
  }

}
```
