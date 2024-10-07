package io.pinger.groups.storage.impl.sql.connection.hikari;

import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.type.StorageType;

public class MariaDbConnectionFactory extends HikariConnectionFactory {

    public MariaDbConnectionFactory(StorageConfig credentials) {
        super(credentials);
    }

    @Override
    protected String getDefaultPort() {
        return "3306";
    }

    @Override
    protected String getDriverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String getDriverIdentifier() {
        return "mariadb";
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MARIADB;
    }
}
