package io.github.mawen12.runtime;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="1181963012mw@gmail.com">mawen12</a>
 * @since 2024/9/17
 */
public class LatchTask implements Runnable {

    private final Runnable runnable;

    private final CountDownLatch latch;

    public LatchTask(Runnable runnable, CountDownLatch latch) {
        this.runnable = runnable;
        this.latch = latch;
    }

    @Override
    public void run() {
        Runtime.pushTask(this);
        try {
            runnable.run();
        } catch (final Throwable t) {
            throw t;
        } finally {
            latch.countDown();
            Runtime.popTask();
        }
    }
}
