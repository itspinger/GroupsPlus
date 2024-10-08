package io.pinger.groups.storage.impl.sql;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.storage.impl.sql.connection.ConnectionFactory;
import io.pinger.groups.user.User;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SqlStorage implements StorageImplementation {
    private static final String LOAD_USER = "SELECT * FROM gp_users WHERE uuid = ?;";
    private static final String SAVE_USER = "INSERT INTO gp_users(uuid) VALUES (?);";
    private static final String LOAD_ASSIGNED_GROUPS = "SELECT * FROM gp_users_groups JOIN gp_groups USING (name) WHERE uuid = ?;";
    private static final String INSERT_ASSIGNED_GROUPS = "INSERT INTO gp_users_groups(uuid, name, expiresAt) VALUES (?, ?, ?);";
    private static final String DELETE_SPECIFIC_ASSIGNED_GROUP = "DELETE FROM gp_users_groups WHERE uuid = ? AND name = ? AND expiresAt = ?;";
    private static final String DELETE_ASSIGNED_GROUPS = "DELETE FROM gp_users_groups WHERE name = ?;";
    private static final String LOAD_GROUPS = "SELECT * FROM gp_groups;";
    private static final String LOAD_SPECIFIC_GROUP = "SELECT * FROM gp_groups WHERE name = ?;";
    private static final String CREATE_GROUP = "INSERT INTO gp_groups(name, prefix, priority) VALUES (?, ?, ?);";
    private static final String UPDATE_GROUP = "UPDATE gp_groups SET prefix = ?, priority = ? WHERE name = ?;";
    private static final String DELETE_GROUP ="DELETE FROM gp_groups WHERE name = ?;";

    private final GroupsPlus groupsPlus;
    private final PluginLogger logger;
    private final ConnectionFactory connectionFactory;

    public SqlStorage(ConnectionFactory connectionFactory) {
        this.groupsPlus = Instances.getOrThrow(GroupsPlus.class);
        this.logger = Instances.getOrThrow(PluginLogger.class);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void init() {
        this.connectionFactory.init();

        final String resourceName = String.format("%s.sql", this.connectionFactory.getStorageType().getIdentifier());

        this.logger.info("Trying to create default tables from file {}", resourceName);

        final InputStream inputStream = this.groupsPlus.getResource(resourceName);
        SqlStorageLoader.init(this.connectionFactory, inputStream);

        this.logger.info("Successfully created default tables from file {}", resourceName);
    }

    @Override
    public void shutdown() {
        this.connectionFactory.shutdown();
    }

    @Override
    public User loadUser(UUID id) throws Exception {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(LOAD_USER)) {
                statement.setString(1, id.toString());
                try (final ResultSet set = statement.executeQuery()) {
                    if (!set.next()) {
                        final User user = new User(id);
                        this.saveUser(connection, user);
                        return user;
                    }

                    final User user = new User(id);
                    final List<AssignedGroup> groups = this.getAssignedGroups(connection, user);
                    user.addAssignedGroups(groups);
                    return user;
                }
            }
        }
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
    public void addGroupToUser(User user, Group group, long expiresAt) throws Exception {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(INSERT_ASSIGNED_GROUPS)) {
                statement.setString(1, user.getId().toString());
                statement.setString(2, group.getName());
                statement.setLong(3, expiresAt);
                statement.executeUpdate();
            }
        }

        final AssignedGroup newGroup = new AssignedGroup(user, group, expiresAt);
        user.addAssignedGroups(newGroup);
    }

    @Override
    public void removeGroupFromUser(User user, AssignedGroup group) throws Exception {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_SPECIFIC_ASSIGNED_GROUP)) {
                statement.setString(1, user.getId().toString());
                statement.setString(2, group.getGroup().getName());
                statement.setLong(3, group.getExpiresAt());
                statement.executeUpdate();
            }
        }

        user.removeAssignedGroup(group);
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
                        final Group group = this.createGroup(connection, new Group(name));
                        this.groupsPlus.getGroupRepository().addGroup(group);
                        return group;
                    }
                }
            }
        }
    }

    @Override
    public void saveGroup(Group group) throws Exception {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(UPDATE_GROUP)) {
                statement.setString(1, group.getPrefix());
                statement.setLong(2, group.getPriority());
                statement.setString(3, group.getName());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void deleteGroup(Group group) throws Exception {
        try (final Connection connection = this.getConnection()) {
            // Delete all groups bound to a player first
            this.deleteAssignedGroups(connection, group);

            try (final PreparedStatement statement = connection.prepareStatement(DELETE_GROUP)) {
                statement.setString(1, group.getName());
                statement.executeUpdate();
            }
        }

        this.groupsPlus.getGroupRepository().unloadGroup(group);
    }

    private void deleteAssignedGroups(Connection connection, Group group) throws Exception {
        try (final PreparedStatement statement = connection.prepareStatement(DELETE_ASSIGNED_GROUPS)) {
            statement.setString(1, group.getName());
            statement.executeUpdate();
        }
    }

    private void saveUser(Connection connection, User user) throws Exception {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_USER)) {
            statement.setString(1, user.getId().toString());
            statement.executeUpdate();
        }
    }

    private List<AssignedGroup> getAssignedGroups(Connection connection, User user) throws SQLException {
        final List<AssignedGroup> groups = new ArrayList<>();
        try (final PreparedStatement statement = connection.prepareStatement(LOAD_ASSIGNED_GROUPS)) {
            statement.setString(1, user.getId().toString());
            try (final ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    final AssignedGroup group = this.getAssignedGroup(set, user);
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    private AssignedGroup getAssignedGroup(ResultSet set, User user) throws SQLException {
        final String groupName = set.getString("name");
        final long expiresAt = set.getLong("expiresAt");

        Group group = this.groupsPlus.getGroupRepository().findGroupByName(groupName);
        if (group == null) {
            group = this.loadGroup(set);
            this.groupsPlus.getGroupRepository().addGroup(group);
        }

        return new AssignedGroup(user, group, expiresAt);
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
