package io.pinger.groups.user;

import io.pinger.groups.config.MessageConfiguration;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class User {
    private final UUID id;
    private final List<AssignedGroup> assignedGroups;

    public User(UUID id, List<AssignedGroup> assignedGroups) {
        this.id = id;
        this.assignedGroups = Collections.synchronizedList(assignedGroups);
    }

    public User(UUID id) {
        this(id, new ArrayList<>());
    }

    public List<AssignedGroup> getAssignedGroups() {
        return Collections.unmodifiableList(this.assignedGroups);
    }

    public List<AssignedGroup> getActiveAssignedGroups() {
        return this.assignedGroups.stream().filter(AssignedGroup::isActive).toList();
    }

    public List<Group> getActiveGroups() {
        return this.getActiveAssignedGroups().stream().map(AssignedGroup::getGroup).toList();
    }

    public UUID getId() {
        return this.id;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    /**
     * This method sends a message within the messages.yml file of the plugin.
     *
     * @param key the key of the message
     */

    public void sendMessage(String key) {
        final MessageConfiguration messages = Instances.get(MessageConfiguration.class);
        if (this.toPlayer() == null || !messages.has(key)) {
            return;
        }

        // Get the message
        final String message = messages.of(key);
        this.toPlayer().sendMessage(message);
    }

    /**
     * This method sends a formatted message from the configuration file
     * to this player.
     * <p>
     * Do not do this if you want to send a message that is not within the messages.yml file.
     *
     * @param key    they key from the config
     * @param format the objects to format
     */

    public void sendMessage(String key, Object... format) {
        final MessageConfiguration messages = Instances.get(MessageConfiguration.class);
        if (this.toPlayer() == null || !messages.has(key)) {
            return;
        }

        // Get the message
        final String message = messages.ofFormatted(key, format);
        this.toPlayer().sendMessage(message);
    }


}
