package io.pinger.groups.storage;

import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.user.User;
import io.pinger.groups.worker.Workers;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

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
            this.logger.error("Failed to init storage implementation", e);
        }
    }

    public void shutdown() {
        try {
            this.implementation.shutdown();
        } catch (Exception e) {
            this.logger.error("Failed to shutdown storage implementation", e);
        }
    }

    public <T> CompletableFuture<T> future(Callable<T> callable) {
        return Workers.get().callAsync(callable);
    }

    public <T> CompletableFuture<T> supplyFuture(Supplier<T> supplier) {
        return Workers.get().supplyAsync(supplier);
    }

    public CompletableFuture<User> loadUser(UUID id) {
        return this.future(() -> this.implementation.loadUser(id));
    }

    public CompletableFuture<Void> loadAllGroups() {
        return this.future(() -> {
            this.implementation.loadAllGroups();
            return null;
        });
    }

    public CompletableFuture<Group> getOrCreateGroup(@NotNull String name) {
        return this.future(() -> this.implementation.createNewGroup(name));
    }

    public CompletableFuture<Void> deleteGroup(@NotNull Group group) {
        return this.future(() -> {
            this.implementation.deleteGroup(group);
            return null;
        });
    }

    public CompletableFuture<Void> updateGroup(@NotNull Group group) {
        return this.future(() -> {
            this.implementation.saveGroup(group);
            return null;
        });
    }

    public CompletableFuture<Void> addGroupToUser(@NotNull User user, @NotNull Group group, long expiry) {
        return this.future(() -> {
            this.implementation.addGroupToUser(user, group, expiry);
            return null;
        });
    }

    public CompletableFuture<Void> removeGroupFromUser(@NotNull User user, @NotNull AssignedGroup group) {
        return this.future(() -> {
            this.implementation.removeGroupFromUser(user, group);
            return null;
        });
    }
}
