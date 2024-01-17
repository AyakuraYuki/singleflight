package cc.ayakurayuki.repo.singleflight;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Call} is an in-flight or completed singleflight call.
 *
 * @author Ayakura Yuki
 */
final class Call<T> {

  /**
   * These fields are written once before the CountDownLatch is counted down
   * and are only read after the CountDownLatch is all cleared.
   */
  private T val;

  private CountDownLatch wg;

  /**
   * These fields are incremented in atomic by {@link #incrementDups()} with
   * the singleflight lock held before the CountDownLatch is all cleared.
   */
  private AtomicInteger dups;

  public T getVal() {
    return val;
  }

  public void setVal(T val) {
    this.val = val;
  }

  public void await() throws InterruptedException {
    this.wg.await();
  }

  public void lock() {
    this.wg = new CountDownLatch(1);
  }

  public void done() {
    this.wg.countDown();
  }

  public int incrementDups() {
    if (this.dups == null) {
      this.dups = new AtomicInteger(1);
      return 1;
    }
    return this.dups.incrementAndGet();
  }

}
