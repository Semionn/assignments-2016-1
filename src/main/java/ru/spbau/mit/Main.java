package ru.spbau.mit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Require file/directory name argument");
            return;
        }

        Path path = Paths.get(args[0]);
        if (Files.exists(path)) {
            calcPerformance(Main::traverse, path);
            calcPerformance(Main::getPathMD5Executors, path);
            calcPerformance(p -> new ForkJoinPool().invoke(new DirectoryTraverseTask(p)), path);
        } else {
            System.out.println("Path does not exists");
        }

    }

    private static void calcPerformance(Function<Path, String> function, Path path) {
        long start = System.currentTimeMillis();
        String result = function.apply(path);
        long end = System.currentTimeMillis();
        System.out.println("Hash: " + result);
        System.out.println("Time: " + Long.toString(end - start) + " ms");
    }

    private static String traverse(Path path) {
        try {
            if (Files.isDirectory(path)) {
                String dirResult = Files.walk(path).skip(1).map(Main::traverse).collect(Collectors.joining());
                dirResult += path.toString();
                return MD5Calculator.getMD5String(dirResult);
            } else {
                return MD5Calculator.getMD5File(path.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getPathMD5Executors(Path path) {
        ExecutorService pool = Executors.newCachedThreadPool();
        String result = traverseExecutors(path, pool);
        pool.shutdown();
        return result;
    }

    private static String traverseExecutors(Path path, ExecutorService pool) {
        try {
            if (Files.isDirectory(path)) {
                String dirResult = Files.walk(path).skip(1)
                        .map(p -> pool.submit(() -> traverseExecutors(p, pool)))
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                return "";
                            }
                        })
                        .collect(Collectors.joining());
                return MD5Calculator.getMD5String(dirResult);
            } else {
                return MD5Calculator.getMD5File(path.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static class DirectoryTraverseTask extends RecursiveTask<String> {
        private Path path;
        DirectoryTraverseTask(Path path) {
            this.path = path;
        }

        @Override
        protected String compute() {
            try {
                if (Files.isDirectory(path)) {
                    List<DirectoryTraverseTask> tasks = Files.walk(path).skip(1)
                            .map(DirectoryTraverseTask::new)
                            .collect(Collectors.toList());
                    tasks.forEach(ForkJoinTask::fork);
                    String dirResult = tasks.stream().map(ForkJoinTask::join).collect(Collectors.joining());
                    return MD5Calculator.getMD5String(dirResult);
                } else {
                    return MD5Calculator.getMD5File(path.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
