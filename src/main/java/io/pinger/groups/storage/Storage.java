package io.pinger.groups.storage;

import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.worker.Workers;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Storage {
    private final StorageImplementation implementation;
    private final PluginLogger logger;

    public Storage(StorageImplementation implementation) {
        this.implementation = implementation;
        this.logger = Instances.getOrThrow(PluginLogger.class);
    }

    public void init() {
        try {
            this.implementation.init();
        } catch (Exception e) {
            this.logger.error("Failed to init storage implementation {}", e);
        }
    }

    public void shutdown() {
        try {
            this.implementation.shutdown();
        } catch (Exception e) {
            this.logger.error("Failed to shutdown storage implementation {}", e);
        }
    }

    public <T> CompletableFuture<T> future(Callable<T> callable) {
        return Workers.get().callAsync(callable);
    }

    public <T> CompletableFuture<T> supplyFuture(Supplier<T> supplier) {
        return Workers.get().supplyAsync(supplier);
    }

    public CompletableFuture<Void> loadAllGroups() {
        return this.future(() -> {
            this.implementation.loadAllGroups();
            return null;
        });
    }

    public CompletableFuture<Group> getOrCreateGroup(String name) {
        return this.future(() -> this.implementation.getOrCreateGroup(name));
    }
}
