package io.pinger.groups.storage.impl.sql.connection.hikari;

import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.type.StorageType;

public class MySqlConnectionFactory extends HikariConnectionFactory {

    public MySqlConnectionFactory(StorageConfig credentials) {
        super(credentials);
    }

    @Override
    protected String getDefaultPort() {
        return "3306";
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String getDriverIdentifier() {
        return "mysql";
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MYSQL;
    }
}
