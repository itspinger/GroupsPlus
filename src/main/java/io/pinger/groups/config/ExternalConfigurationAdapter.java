package io.pinger.groups.config;

import io.pinger.groups.GroupsPlus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ExternalConfigurationAdapter {

    protected YamlConfiguration configuration;

    public ExternalConfigurationAdapter(GroupsPlus plugin, String name)  {
        this(plugin, name, false);
    }

    public ExternalConfigurationAdapter(GroupsPlus plugin, String name, boolean load) {
        final File file = new File(plugin.getDataFolder(), name);
        this.configuration = YamlConfiguration.loadConfiguration(file);

        // If the load is false
        // We shouldn't replace if the file is not empty
        if (!load && file.length() > 0) {
            return;
        }

        // Load resource by this name
        // And add the configuration section
        final InputStream inputStream = plugin.getResource(name);
        if (inputStream == null) {
            throw new IllegalArgumentException("File resource cannot be found (" + name + ")");
        }

        // Try to read from the input stream
        // And add to the file
        try (final Reader reader = new InputStreamReader(inputStream)) {
            final YamlConfiguration keyed = YamlConfiguration.loadConfiguration(reader);

            // If we shouldn't copy the defaults, return here
            // Copy the defaults
            this.configuration.options().copyDefaults(true);

            // Try to add defaults from resourceConfig
            // To the main configuration, by looping over the keys
            for (final Map.Entry<String, Object> entries : keyed.getValues(true).entrySet()) {
                this.configuration.addDefault(entries.getKey(), entries.getValue());
            }

            this.configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().info("Failed to create a file with name " + name);
        }
    }

}
