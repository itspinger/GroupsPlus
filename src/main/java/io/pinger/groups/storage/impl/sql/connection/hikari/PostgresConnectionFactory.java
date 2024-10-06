package io.pinger.groups.storage.impl.sql.connection.hikari;

import io.pinger.groups.storage.config.StorageConfig;

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

}
