package io.github.ayakurayuki.singleflight;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author Ayakura Yuki
 * @date 2024/01/16-11:09
 */
public class Singleflight {

  private final Lock mu = new ReentrantLock();

  private ConcurrentHashMap<String, Call> m;

  @SuppressWarnings("unchecked")
  public <T> T run(String key, Supplier<T> func) throws InterruptedException {
    this.mu.lock();
    if (this.m == null) {
      this.m = new ConcurrentHashMap<>();
    }

    Call<T> c = this.m.get(key);
    if (c != null) {
      c.incrementDups();
      this.mu.unlock();
      c.await();
      return c.getVal();
    }

    c = new Call<>();
    c.lock();
    this.m.put(key, c);
    this.mu.unlock();

    c.setVal(func.get());
    c.done();

    this.mu.lock();
    this.m.remove(key);
    this.mu.unlock();

    return c.getVal();
  }

}
