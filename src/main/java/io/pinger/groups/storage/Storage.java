package io.pinger.groups.storage;

import io.pinger.groups.instance.Instances;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.storage.impl.StorageImplementation;

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

}
