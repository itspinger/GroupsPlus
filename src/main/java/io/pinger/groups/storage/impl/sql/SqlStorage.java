package io.pinger.groups.storage.impl.sql;

import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.storage.impl.sql.connection.ConnectionFactory;

public class SqlStorage implements StorageImplementation {
    private final ConnectionFactory connectionFactory;

    public SqlStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void init() {
        this.connectionFactory.init();
    }

    @Override
    public void shutdown() {
        this.connectionFactory.shutdown();
    }
}
