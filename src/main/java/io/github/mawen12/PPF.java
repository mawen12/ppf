package io.github.mawen12;

import io.github.mawen12.runtime.LatchTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
}
