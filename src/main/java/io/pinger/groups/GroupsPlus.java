package io.pinger.groups;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import com.tchristofferson.configupdater.ConfigUpdater;
import io.pinger.groups.commands.GroupManagerCommand;
import io.pinger.groups.commands.GroupsCommand;
import io.pinger.groups.commands.provider.GroupProvider;
import io.pinger.groups.commands.provider.TimeArgumentProvider;
import io.pinger.groups.commands.provider.UserProvider;
import io.pinger.groups.config.MessageConfiguration;
import io.pinger.groups.dependenies.DependencyManager;
import io.pinger.groups.group.Group;
import io.pinger.groups.group.GroupRepository;
import io.pinger.groups.instance.Instances;
import io.pinger.groups.listener.PlayerListener;
import io.pinger.groups.logger.SpigotPluginLogger;
import io.pinger.groups.storage.Storage;
import io.pinger.groups.storage.StorageFactory;
import io.pinger.groups.storage.config.StorageConfig;
import io.pinger.groups.storage.impl.StorageImplementation;
import io.pinger.groups.storage.type.StorageType;
import io.pinger.groups.timer.Timer;
import io.pinger.groups.user.User;
import io.pinger.groups.user.UserManager;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GroupsPlus extends JavaPlugin {
    private MessageConfiguration messageConfiguration;
    private GroupRepository groupRepository;
    private UserManager userManager;
    private SpigotPluginLogger logger;
    private Storage storage;

    @Override
    public void onEnable() {
        Instances.register(this);

        new DependencyManager(this).loadDependencies();

        this.logger = new SpigotPluginLogger(this.getLogger());
        this.addDefaultConfig();
        this.tryInitStorage();

        this.groupRepository = new GroupRepository(this);
        this.messageConfiguration = new MessageConfiguration(this);
        this.userManager = new UserManager(this);

        this.loadAllGroups();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        if (this.storage != null) {
            this.storage.shutdown();
        }
    }

    private void loadAllGroups() {
        if (!this.isDatabaseEnabled()) {
            return;
        }

        try {
            this.storage.loadAllGroups().get();
        } catch (Exception e) {
            this.logger.info("Failed to load all groups from the database ", e);
            return;
        }

        this.logger.info("Successfully loaded {} groups from the database", this.groupRepository.getAllGroups().size());
    }

    private void registerCommands() {
        final CommandService service = Drink.get(this);
        service.bind(Group.class).toProvider(new GroupProvider(this));
        service.bind(Timer.class).toProvider(new TimeArgumentProvider());
        service.bind(User.class).toProvider(new UserProvider(this));
        service.register(new GroupManagerCommand(this), "groupmanager", "gm");
        service.register(new GroupsCommand(this), "groups");
        service.registerCommands();
    }

    public boolean isDatabaseEnabled() {
        return this.storage != null;
    }

    @NotNull
    public SpigotPluginLogger getPluginLogger() {
        return this.logger;
    }

    public Storage getStorage() {
        return this.storage;
    }

    @NotNull
    public GroupRepository getGroupRepository() {
        return this.groupRepository;
    }

    @NotNull
    public MessageConfiguration getMessageConfiguration() {
        return this.messageConfiguration;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }

    private void addDefaultConfig() {
        this.saveDefaultConfig();
        final File config = new File(this.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", config, new ArrayList<>());
        } catch (Exception e) {
            this.logger.error("Failed to update the config", e);
        }

        this.logger.info("Successfully loaded the config.yml");
        this.reloadConfig();
    }

    private void tryInitStorage() {
        final StorageConfig config = this.loadStorageConfig();
        if (config == null) {
            return;
        }

        final String type = this.getConfig().getString("database.type", StorageType.UNKNOWN.getIdentifier());
        final StorageType storageType = StorageType.fromIdentifier(type);
        if (storageType == StorageType.UNKNOWN) {
            this.logger.info("Failed to recognize this type of storage, disabling!!!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        final StorageFactory factory = new StorageFactory(config);
        final StorageImplementation implementation;

        try {
            implementation = factory.createStorage(storageType);
        } catch (Exception e) {
            this.logger.info("Failed to create storage implementation {}", e);
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        this.storage = new Storage(implementation);
        this.storage.init();
    }

    private StorageConfig loadStorageConfig() {
        if (!this.getConfig().getBoolean("database.enabled")) {
            return null;
        }

        final ConfigurationSection section = this.getConfig().getConfigurationSection("database");
        if (section == null) {
            this.getLogger().log(Level.SEVERE, "Failed to create a database");
            this.getPluginLoader().disablePlugin(this);
            return null;
        }

        final String address = section.getString("address");
        final String database = section.getString("database");
        final String username = section.getString("username");
        final String password = section.getString("password");

        final ConfigurationSection pool = section.getConfigurationSection("hikari-pool");
        if (pool == null) {
            return new StorageConfig(address, database, username, password);
        }

        return new StorageConfig(
            address,
            database,
            username,
            password,
            pool.getInt("maximum-pool-size"),
            pool.getInt("minimum-idle"),
            pool.getInt("maximum-lifetime"),
            pool.getInt("keep-alive-time"),
            pool.getInt("connection-timeout")
        );
    }
}
