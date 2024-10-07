package io.pinger.groups.storage.impl.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    void init();

    void shutdown();

    Connection getConnection() throws SQLException;

}
