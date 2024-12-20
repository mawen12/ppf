package io.github.mawen12.runtime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class PreloadConsumer<V> implements Runnable {

    private Consumer<V> consumer;

    private Semaphore semaphore;

    private BlockingQueue<V> queue;

    private CountDownLatch done;

    public PreloadConsumer(Consumer<V> consumer, Semaphore semaphore, BlockingQueue<V> queue, CountDownLatch done) {
        this.consumer = consumer;
        this.semaphore = semaphore;
        this.queue = queue;
        this.done = done;
    }

    @Override
    public void run() {
        V task = null;
        while (true) {
            try {
                task = queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (task == null) {
                break;
            }

            semaphore.release();
            consumer.accept(task);
        }
        done.countDown();
    }
}
