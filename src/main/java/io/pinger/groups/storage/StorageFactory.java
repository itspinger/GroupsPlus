package io.pinger.groups.storage;

import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.storage.impl.sql.SqlStorage;
import io.pinger.groups.storage.impl.sql.connection.ConnectionFactory;
import io.pinger.groups.storage.impl.sql.connection.hikari.MariaDbConnectionFactory;
import io.pinger.groups.storage.impl.sql.connection.hikari.MySqlConnectionFactory;
import io.pinger.groups.storage.type.StorageType;

public class StorageFactory {
    private final StorageConfig config;

    public StorageFactory(StorageConfig config) {
        this.config = config;
    }

    public StorageImplementation createStorage(StorageType type) {
        switch (type) {
            case MYSQL, MARIADB -> {
                final ConnectionFactory factory = this.createConnectionFactory(type);
                return new SqlStorage(factory);
            }
            default -> throw new IllegalStateException("Unknown storage type: " + type);
        }
    }

    private ConnectionFactory createConnectionFactory(StorageType type) {
        return switch (type) {
            case MYSQL -> new MySqlConnectionFactory(this.config);
            case MARIADB -> new MariaDbConnectionFactory(this.config);
            default -> throw new IllegalStateException("Unknown storage type: " + type);
        };
    }

}
