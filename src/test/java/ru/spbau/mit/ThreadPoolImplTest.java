package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by semionn on 29.04.16.
 */
public class ThreadPoolImplTest {

    private static final int THREAD_CNT = 10;
    private static final int TASK_CNT = 1000;
    private static final int TEST_STABLE_CNT = 100;
    private static final int SLEEP_TIME = 10;
    private static final int WAIT_TIME = 1000;

    @Test
    public void testSubmit() throws Exception {
        for (int testNum = 0; testNum < TEST_STABLE_CNT; testNum++) {
            final ThreadPool threadPool = new ThreadPoolImpl(THREAD_CNT);
            final List<LightFuture<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_CNT; i++) {
                final int t = i;
                LightFuture<Integer> future = threadPool.submit(() -> t);
                futures.add(future);
                futures.add(future.thenApply(x -> x * 2));
            }
            Integer k = 0;
            for (int i = 0; i < TASK_CNT; i++) {
                Integer res1 = futures.get(i * 2).get();
                Integer res2 = futures.get(i * 2 + 1).get();
                assertTrue(res1.equals(k));
                assertTrue(res2.equals(k * 2));
                k++;
            }
            threadPool.shutdown();
        }
    }

    @Test
    public void testShutdown() throws Exception {
        for (int testNum = 0; testNum < 1; testNum++) {
            final ThreadPool threadPool = new ThreadPoolImpl(THREAD_CNT);

            final List<LightFuture<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_CNT; i++) {
                LightFuture<Integer> future = threadPool.submit(() -> 0);
                futures.add(future);
            }
            threadPool.shutdown();
            for (int i = 0; i < TASK_CNT; i++) {
                LightFuture<Integer> future = threadPool.submit(() -> 0);
                assertTrue(future == null);
            }

            for (LightFuture<Integer> future : futures) {
                waitFuture(future, WAIT_TIME);
                assertTrue(future.isReady());
            }
        }
    }

    @Test
    public void testReadyAndThenApply() throws LightExecutionException, InterruptedException {
        for (int testNum = 0; testNum < TEST_STABLE_CNT; testNum++) {
            final ThreadPool threadPool = new ThreadPoolImpl(THREAD_CNT);
            final LightFuture<Integer> task = threadPool.submit(() -> {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) { }
                return 1;
            });
            final LightFuture<Integer> taskThen = task.thenApply(x -> {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) { }
                return x + 1;
            });
            final LightFuture<Integer> taskDoubleThen = taskThen.thenApply(x -> x - 2);

            assertEquals(1, (int) task.get());
            assertTrue(task.isReady());
            assertFalse(taskDoubleThen.isReady());

            assertEquals(2, (int) taskThen.get());
            assertTrue(taskThen.isReady());

            assertEquals(0, (int) taskDoubleThen.get());
            assertTrue(taskDoubleThen.isReady());
            threadPool.shutdown();
        }
    }

    @Test
    public void testThenApplyOptimize() throws InterruptedException {
        final ThreadPool threadPool = new ThreadPoolImpl(2);
        final LightFuture<Integer> task = threadPool.submit(() -> {
            while (true) { }
        });
        task.thenApply(Function.identity());
        final LightFuture<Boolean> task2 = threadPool.submit(() -> true);
        waitFuture(task2, WAIT_TIME);
        assertTrue(task2.isReady());
        threadPool.shutdown();
    }

    @Test
    public void testThreadCount() throws InterruptedException {
        final ThreadPool threadPool = new ThreadPoolImpl(THREAD_CNT);
        final CyclicBarrier barrier = new CyclicBarrier(THREAD_CNT);
        final List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_CNT; i++) {
            futures.add(threadPool.submit(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }));
        }
        for (LightFuture<Integer> future : futures) {
            waitFuture(future, WAIT_TIME);
            assertTrue(future.isReady());
        }
        threadPool.shutdown();
    }

    private void waitFuture(LightFuture<?> future, long timeout) throws InterruptedException {
        if (!future.isReady()) {
            Runnable taskWait = () -> {
                try {
                    future.get();
                    synchronized (this) {
                        notify();
                    }
                } catch (LightExecutionException | InterruptedException e) {
                }
            };
            new Thread(taskWait).start();
            synchronized (taskWait) {
                taskWait.wait(timeout);
            }
        }
    }
}
