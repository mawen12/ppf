package io.github.mawen12;

import io.github.mawen12.runtime.LatchTask;
import io.github.mawen12.runtime.PreloadConsumer;
import io.github.mawen12.runtime.PreloadProducer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author <a href="1181963012mw@gmail.com">mawen12</a>
 * @since 2024/9/17
 */
public class PPF {

    private ExecutorService executorService;

    public PPF(ExecutorService executorService) {
        assert executorService != null;
        this.executorService = executorService;
    }

    /**
     * all task will run at async
     *
     * @param tasks
     * @throws InterruptedException
     */
    public void asyncAll(final Runnable... tasks) throws InterruptedException {
        int length = tasks.length;
        CountDownLatch latch = new CountDownLatch(length);

        for (Runnable task : tasks) {
            executorService.submit(new LatchTask(task, latch));
        }

        latch.await();
    }

    /**
     * all task will run at async, but if timeout, all task will be cancelled.
     *
     * @param tasks
     * @param timeout
     * @param timeUnit
     * @throws InterruptedException
     */
    public void asyncAll(final Runnable[] tasks, long timeout, TimeUnit timeUnit) throws InterruptedException {
        int length = tasks.length;
        CountDownLatch latch = new CountDownLatch(length);
        List<Future<?>> futures = new ArrayList<>(length);

        for (Runnable task : tasks) {
            futures.add(executorService.submit(new LatchTask(task, latch)));
        }

        latch.await(timeout, timeUnit);
        futures.forEach(future -> future.cancel(true));
    }

    public <V> void preload(final Iterator<V> producer, Consumer<V> consumer, final int preload) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(2);
        Semaphore semaphore = new Semaphore(preload);
        BlockingQueue<V> queue = new LinkedBlockingQueue<>(preload);

        PreloadProducer<V> p = new PreloadProducer<>(producer, semaphore, queue, latch);
        PreloadConsumer<V> c = new PreloadConsumer<>(consumer, semaphore, queue, latch);
        executorService.submit(p);
        executorService.submit(c);

        latch.await();
    }

    public void mostOnce(final Runnable[] tasks) throws InterruptedException {
        int length = tasks.length;
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>(length);

        for (Runnable task : tasks) {
            futures.add(executorService.submit(new LatchTask(task, latch)));
        }

        latch.await();
        futures.forEach(future -> future.cancel(true));
    }

    public <T> void mostOnce(final List<T> list, Consumer<T> task) throws InterruptedException {
        Runnable[] tasks = new Runnable[list.size()];
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            tasks[i] = () -> task.accept(t);
        }

        mostOnce(tasks);
    }
}
