package ru.spbau.mit;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final List<Thread> threads = new LinkedList<>();
    private Queue<LightFutureImpl> futures = new LinkedList<>();
    private volatile boolean stopped = false;

    public ThreadPoolImpl(int n) {
        for (int i = 0; i < n; i++) {
            Thread newThread = new Thread(new ThreadWorker());
            threads.add(newThread);
            newThread.start();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        if (!stopped) {
            LightFutureImpl<R> future = new LightFutureImpl<>(supplier);
            addFuture(future);
            return future;
        }
        return null;
    }

    @Override
    public synchronized void shutdown() {
        stopped = true;
        while (futures.size() != 0) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }

        threads.forEach(Thread::interrupt);
    }

    private synchronized void addFuture(LightFutureImpl future) {
        futures.add(future);
        notify();
    }

    private synchronized <R> LightFutureImpl<R> peekFuture() throws InterruptedException {
        while (futures.size() == 0) {
            wait();
        }

        LightFutureImpl<R> future = futures.peek();
        futures.poll();

        if (futures.size() == 0 && stopped) {
            notifyAll();
        }

        return future;
    }

    private final class ThreadWorker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    LightFutureImpl future = ThreadPoolImpl.this.peekFuture();
                    future.run();
                    synchronized (future) {
                        future.notifyAll();
                    }
                }
            } catch (InterruptedException e) { }
        }
    }

    private final class LightFutureImpl<R> implements LightFuture<R> {
        private volatile R result = null;
        private volatile boolean ready = false;
        private volatile Queue<LightFutureImpl> thenApplyTasks = new LinkedList<>();
        private final Runnable task;
        private Throwable taskException;

        LightFutureImpl(final Supplier<R> supplier) {
            task = () -> {
                try {
                    result = supplier.get();
                } catch (Exception e) {
                    taskException = e;
                }

                ready = true;

                synchronized (this) {
                    thenApplyTasks.forEach(ThreadPoolImpl.this::addFuture);
                    thenApplyTasks.clear();
                }
            };
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public synchronized R get() throws LightExecutionException, InterruptedException {
            while (!ready) {
                wait();
            }

            if (taskException != null) {
                throw new LightExecutionException(taskException);
            }
            return result;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            if (stopped) {
                return null;
            }
            LightFutureImpl<U> future = new LightFutureImpl<>(() -> {
                try {
                    R firstRes = get();
                    return f.apply(firstRes);
                } catch (LightExecutionException | InterruptedException e) {
                    taskException = e;
                    throw new RuntimeException(e);
                }
            });

            if (isReady()) {
                addFuture(future);
            } else {
                synchronized (this) {
                    if (isReady()) {
                        addFuture(future);
                    } else {
                        thenApplyTasks.add(future);
                    }
                }
            }
            return future;
        }

        private void run() {
            task.run();
        }

    }

}
