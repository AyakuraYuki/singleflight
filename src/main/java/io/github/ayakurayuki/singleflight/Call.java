package io.github.ayakurayuki.singleflight;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ayakura Yuki
 * @date 2024/01/16-11:06
 */
final class Call<T> {

  private T val;

  private CountDownLatch wg;

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
