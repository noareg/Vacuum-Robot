package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * <p>
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

    private T result = null;
    private volatile boolean isDone = false;
    private final Object lock = new Object();


    /**
     * This should be the the only public constructor in this class.
     */
    public Future() {
    }

    /**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     *
     * @return return the result of type T if it is available, if not wait until it is available.
     */
    public T get() {
        synchronized (lock) {
            while (!this.isDone) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // closing action
                    return null;
                }
            }
            return this.result;
        }
    }

    /**
     * Resolves the result of this Future object.
     */

    public void resolve(T result) {
        synchronized (lock) {
            if (!this.isDone) {
                this.result = result;
                this.isDone = true;
                lock.notifyAll();
            }
        }
    }

    /**
     * @return true if this object has been resolved, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit   the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not,
     * wait for {@code timeout} TimeUnits {@code unit}. If time has
     * elapsed, return null.
     */
    public T get(long timeout, TimeUnit unit) {
        synchronized (lock) {
            long timeoutMillis = unit.toMillis(timeout); //convert
            long endTime = System.currentTimeMillis() + timeoutMillis;
            while (!isDone) {
                long remainingTime = endTime - System.currentTimeMillis();
                if (remainingTime <= 0) {
                    return null;
                }
                try {
                    lock.wait(remainingTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // closing action
                    return null;
                }
            }
            return result;
        }
    }

}
