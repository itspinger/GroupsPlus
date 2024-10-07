package io.pinger.groups.storage.impl.sql;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.storage.impl.sql.connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqlStorage implements StorageImplementation {
    private static final String LOAD_GROUPS = "SELECT * FROM groups;";
    private static final String LOAD_SPECIFIC_GROUP = "SELECT * FROM groups WHERE name = ?;";
    private static final String CREATE_GROUP = "INSERT INTO groups(name, prefix, priority) VALUES (?, ?, ?);";
    private static final String UPDATE_GROUP = "UPDATE groups SET prefix = ?, priority = ? WHERE name = ?;";
    private static final String DELETE_GROUP ="DELETE FROM groups WHERE name = ?;";

    private final GroupsPlus groupsPlus;
    private final ConnectionFactory connectionFactory;

    public SqlStorage(ConnectionFactory connectionFactory) {
        this.groupsPlus = Instances.getOrThrow(GroupsPlus.class);
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

    @Override
    public void loadAllGroups() throws SQLException {
        final Map<String, Group> loadedGroups = new HashMap<>();
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(LOAD_GROUPS)) {
                try (final ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        final Group group = this.loadGroup(set);
                        loadedGroups.put(group.getName(), group);
                    }
                }
            }
        }

        this.groupsPlus.getGroupRepository().overrideGroups(loadedGroups);
    }

    @Override
    public Group createNewGroup(String name) throws SQLException {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(LOAD_SPECIFIC_GROUP)) {
                statement.setString(1, name);
                try (final ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        return this.loadGroup(set);
                    } else {
                        return this.createGroup(connection, new Group(name));
                    }
                }
            }
        }
    }

    @Override
    public void saveGroup(Group group) throws Exception {
        if (!this.existsGroup(group)) {
            return;
        }

        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(UPDATE_GROUP)) {
                statement.setString(1, group.getPrefix());
                statement.setLong(2, group.getPriority());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void deleteGroup(Group group) throws Exception {
        if (!this.existsGroup(group)) {
            return;
        }

        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_GROUP)) {
                statement.setString(1, group.getName());
                statement.executeUpdate();
            }
        }
    }

    private boolean existsGroup(Group group) throws SQLException {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(LOAD_SPECIFIC_GROUP)) {
                statement.setString(1, group.getName());
                try (final ResultSet set = statement.executeQuery()) {
                    return set.next();
                }
            }
        }
    }

    private Group loadGroup(ResultSet set) throws SQLException {
        return new Group(set.getString("name"), set.getString("prefix"), set.getInt("priority"));
    }

    private Group createGroup(Connection connection, Group group) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(CREATE_GROUP)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getPrefix());
            statement.setLong(3, group.getPriority());
            statement.executeUpdate();
        }

        return group;
    }

    private Connection getConnection() throws SQLException {
        return this.connectionFactory.getConnection();
    }
}
