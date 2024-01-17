package cc.ayakurayuki.repo.singleflight;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * {@link Singleflight} provides a duplicate function call suppression
 * mechanism.
 *
 * @author Ayakura Yuki
 */
public class Singleflight {

  /**
   * protects {@link #m}
   */
  private final Lock mu = new ReentrantLock();

  /**
   * lazily initialized
   */
  private ConcurrentHashMap<String, Call> m;

  /**
   * Executes and returns the results of the given {@link Supplier<T>}, making
   * sure that only one execution is in-flight for a given key at a time.
   * <p>
   * If a duplicate comes in, the duplicate caller waits for the original to
   * complete and receives the same results.
   * <p>
   * The return value shared indicates whether result was given to multiple
   * callers.
   *
   * @param key  a unique given key to the execution
   * @param func supplier
   * @param <T>  type of the result from supplier
   *
   * @return results of the given {@link Supplier}
   *
   * @throws InterruptedException if the duplicate caller is interrupted
   *                              while waiting
   */
  @SuppressWarnings("unchecked")
  public <T> T run(String key, Supplier<T> func)
      throws InterruptedException {
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

  /**
   * Tells the singleflight to forget about a key.
   * <p>
   * Future calls to {@link #run(String, Supplier)} for this key will call the
   * function rather than waiting for an earlier call to complete.
   *
   * @param key a unique given key to the execution
   */
  public void forget(String key) {
    this.mu.lock();
    this.m.remove(key);
    this.mu.unlock();
  }

}
