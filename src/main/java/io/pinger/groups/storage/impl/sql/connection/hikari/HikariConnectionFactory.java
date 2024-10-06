package io.pinger.groups.storage.impl.sql.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.impl.sql.connection.ConnectionFactory;

public abstract class HikariConnectionFactory implements ConnectionFactory {
    private final StorageConfig credentials;

    private HikariDataSource source;

    protected HikariConnectionFactory(StorageConfig credentials) {
        this.credentials = credentials;
    }

    protected abstract String getDefaultPort();

    protected abstract String getDriverClassName();

    protected abstract String getDriverIdentifier();

    @Override
    public void init()  {
        final HikariConfig config = new HikariConfig();
        config.setPoolName("groupsplus-pool");

        this.setupJdbcUrl(config);
        config.setDriverClassName(this.getDriverClassName());
        config.setUsername(this.credentials.getUsername());
        config.setPassword(this.credentials.getPassword());

        config.setMaximumPoolSize(this.credentials.getMaxPoolSize());
        config.setMinimumIdle(this.credentials.getMinIdleConnections());
        config.setMaxLifetime(this.credentials.getMaxLifetime());
        config.setKeepaliveTime(this.credentials.getKeepAliveTime());
        config.setConnectionTimeout(this.credentials.getConnectionTimeout());

        this.source = new HikariDataSource(config);
    }

    @Override
    public void shutdown() {
        if (this.source != null) {
            this.source.close();
        }
    }

    private void setupJdbcUrl(HikariConfig config) {
        final String[] split = this.credentials.getAddress().split(":");
        final String host = split[0];
        final String port = split.length > 1 ? split[1] : this.getDefaultPort();

        // Example of jdbc url for mysql
        // jdbc:mysql://host:port/database
        config.setJdbcUrl(
            String.format(
                "jdbc:%s://%s:%s/%s",
                this.getDriverIdentifier(), host, port, this.credentials.getDatabase()
            )
        );
    }
}
