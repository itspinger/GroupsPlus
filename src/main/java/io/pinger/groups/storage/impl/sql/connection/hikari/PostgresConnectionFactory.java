package io.pinger.groups.storage.impl.sql.connection.hikari;

import io.pinger.groups.processor.Processor;
import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.type.StorageType;

public class PostgresConnectionFactory extends HikariConnectionFactory {

    public PostgresConnectionFactory(StorageConfig credentials) {
        super(credentials);
    }

    @Override
    protected String getDefaultPort() {
        return "5432";
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String getDriverIdentifier() {
        return "postgresql";
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.POSTGRESQL;
    }
}
