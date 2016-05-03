package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final Queue<ThreadWorker> freeWorkers;
    private final Queue<LightFutureImpl> futures;
    private final Runnable tasksManager;
    private volatile boolean stopped;

    public ThreadPoolImpl(int n) {
        stopped = false;
        freeWorkers = new LinkedList<>();
        futures = new LinkedList<>();
        List<Thread> threads = new ArrayList<>();
        List<ThreadWorker> threadWorkers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ThreadWorker threadWorker = new ThreadWorker();
            threadWorkers.add(threadWorker);
            threads.add(new Thread(threadWorker));
        }
        tasksManager = new TasksManager(threads, threadWorkers);
        new Thread(tasksManager).start();
        threads.forEach(Thread::start);
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        LightFutureImpl<R> result = null;
        if (!stopped) {
            result = new LightFutureImpl<>(supplier);
            addFeature(result);
        }
        return result;
    }

    @Override
    public void shutdown() {
        synchronized (tasksManager) {
            stopped = true;
            tasksManager.notifyAll();
        }
    }

    private void addFeature(LightFutureImpl future) {
        synchronized (tasksManager) {
            synchronized (futures) {
                futures.add(future);
            }
            tasksManager.notifyAll();
        }
    }

    private final class ThreadWorker implements Runnable {
        private LightFutureImpl lightFuture;

        ThreadWorker() {
        }

        public void setLightFuture(LightFutureImpl lightFuture) {
            this.lightFuture = lightFuture;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (this) {
                        synchronized (tasksManager) {
                            synchronized (freeWorkers) {
                                freeWorkers.add(this);
                                tasksManager.notifyAll();
                            }
                        }
                        wait();
                    }
                    Object res = lightFuture.getTask().get();
                    synchronized (lightFuture) {
                        lightFuture.setResult(res);
                        lightFuture.setReady(true);
                        lightFuture.notifyAll();
                    }
                }
            } catch (InterruptedException e) { }
        }
    }

    private final class LightFutureImpl<R> implements LightFuture<R> {
        private volatile R result = null;
        private volatile boolean ready = false;
        private final Supplier<R> supplier;
        private Throwable taskException;

        LightFutureImpl(final Supplier<R> supplier) {
            this.supplier = supplier;
        }

        public Supplier<R> getTask() {
            return supplier;
        }

        public void setResult(R result) {
            this.result = result;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public R get() throws LightExecutionException, InterruptedException {
            if (!ready) {
                synchronized (this) {
                    while (!ready && !stopped) {
                        wait();
                    }
                }
            }
            if (taskException != null) {
                throw new LightExecutionException(taskException);
            }
            if (stopped) {
                throw new InterruptedException();
            }

            return result;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            if (stopped) {
                return null;
            }
            LightFutureImpl<U> result = new LightFutureImpl<>(() -> {
                try {
                    R firstRes = get();
                    return f.apply(firstRes);
                } catch (LightExecutionException | InterruptedException e) {
                    taskException = e;
                }
                return null;
            });
            addFeature(result);
            return result;
        }
    }

    private final class TasksManager implements Runnable {
        private final List<Thread> threads;
        private final List<ThreadWorker> threadWorkers;
        TasksManager(List<Thread> threads, List<ThreadWorker> threadWorkers) {
            this.threads = threads;
            this.threadWorkers = threadWorkers;
        }

        @Override
        public void run() {
            try {
                synchronized (this) {
                    while (true) {
                        wait();
                        if (stopped) {
                            break;
                        }
                        synchronized (futures) {
                            if (!futures.isEmpty()) {
                                synchronized (freeWorkers) {
                                    if (!freeWorkers.isEmpty()) {
                                        ThreadWorker threadWorker = freeWorkers.poll();
                                        synchronized (threadWorker) {
                                            LightFutureImpl future = futures.poll();
                                            threadWorker.setLightFuture(future);
                                            threadWorker.notify();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    threads.forEach(Thread::interrupt);
                    threadWorkers.forEach(worker -> {
                        if (worker.lightFuture != null) {
                            synchronized (worker.lightFuture) {
                                worker.lightFuture.notifyAll();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) { }
        }
    }

}
