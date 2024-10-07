package io.pinger.groups.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class Workers {
    private final Executor executor;

    private static final Workers DEFAULT_WORKER;

    static {
        DEFAULT_WORKER = register("plus", 16);
    }

    public Workers(Executor executor) {
        this.executor = executor;
    }

    public static Workers get() {
        return Workers.DEFAULT_WORKER;
    }

    public static Workers register(@NotNull String name) {
        return Workers.register(name, Runtime.getRuntime().availableProcessors());
    }

    public static Workers register(@NotNull String name, int threads) {
        return Workers.register(() -> {
            final AtomicInteger count = new AtomicInteger(0);
            final ForkJoinWorkerThreadFactory factory = pool -> {
                final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                worker.setName(String.format("%s-%s", name, count.incrementAndGet()));
                worker.setDaemon(true);
                return worker;
            };

            return new ForkJoinPool(threads, factory, null, false);
        });
    }

    public static Workers register(Supplier<ExecutorService> supplier) {
        return new Workers(supplier.get());
    }

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return this.catchException(CompletableFuture.supplyAsync(supplier, this.executor));
    }

    public <T> CompletableFuture<T> callAsync(Callable<T> callable) {
        return this.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> CompletableFuture<T> catchException(final CompletableFuture<T> future) {
        return future.exceptionally((error) -> {
            error.printStackTrace();
            return null;
        });
    }

}
