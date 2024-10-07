package io.pinger.groups.config;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.instance.Instances;
import org.bukkit.ChatColor;

public class MessageConfiguration extends ExternalConfigurationAdapter {

    public MessageConfiguration(GroupsPlus plugin) {
        super(plugin, "messages.yml", true);

        Instances.registerWithoutSuper(this);
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean has(String key) {
        return this.configuration.get(key) != null;
    }

    public String of(String key, boolean translate) {
        String value = this.configuration.getString(key);

        // Update value
        value = value == null ? "" : value;

        if (!translate)
            return ChatColor.stripColor(value);

        // Out this
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String of(String key) {
        return this.of(key, true);
    }

    private String ofFormatted(String key, boolean translate, Object... objects) {
        return String.format(this.of(key, translate), objects);
    }

    public String ofFormatted(String key, Object... objects) {
        return this.ofFormatted(key, true, objects);
    }


}
