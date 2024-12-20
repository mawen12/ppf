package io.github.mawen12.runtime;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class PreloadProducer<V> implements Runnable {

    private Iterator<V> producer;

    private Semaphore semaphore;

    private BlockingQueue<V> queue;

    private CountDownLatch done;

    public PreloadProducer(Iterator<V> producer, Semaphore semaphore, BlockingQueue<V> queue, CountDownLatch done) {
        this.queue = queue;
        this.semaphore = semaphore;
        this.producer = producer;
        this.done = done;
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            V task = producer.next();
            if (task == null) {
                semaphore.release();
                break;
            }

            try {
                queue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        done.countDown();
    }
}
