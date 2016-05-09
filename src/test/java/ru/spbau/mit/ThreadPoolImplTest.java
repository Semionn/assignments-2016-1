package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by semionn on 29.04.16.
 */
public class ThreadPoolImplTest {

    private static final int THREAD_CNT = 10;
    private static final int TASK_CNT = 1000;
    private static final int TEST_STABLE_CNT = 100;
    private static final int TASK_RESULT = 42;
    private static final int EXTRA_TASK_CNT = 10;
    private static final int SLEEP_TIME = 10;

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
        }
    }

    @Test
    public void testShutdown() throws Exception {
        for (int testNum = 0; testNum < TEST_STABLE_CNT; testNum++) {
            final ThreadPool threadPool = new ThreadPoolImpl(THREAD_CNT);
            final LightFuture<Integer> firstTask = threadPool.submit(() -> TASK_RESULT);
            threadPool.shutdown();
            for (int j = 0; j < EXTRA_TASK_CNT; j++) {
                assertTrue(threadPool.submit(() -> 0) == null);
            }
            try {
                Integer result = firstTask.get();
                assertTrue(firstTask.isReady() && result == TASK_RESULT);
            } catch (InterruptedException e) { }
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
            assertFalse(taskThen.isReady());
            assertFalse(taskDoubleThen.isReady());

            assertEquals(2, (int) taskThen.get());
            assertTrue(taskThen.isReady());

            assertEquals(0, (int) taskDoubleThen.get());
            assertTrue(taskDoubleThen.isReady());
        }
    }
}
