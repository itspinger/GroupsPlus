package io.pinger.groups.storage.impl.sql.connection;

import io.pinger.groups.storage.type.StorageType;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    StorageType getStorageType();

    void init();

    void shutdown();

    Connection getConnection() throws SQLException;

}
