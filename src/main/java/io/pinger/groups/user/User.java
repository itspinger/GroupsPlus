package io.pinger.groups.user;

import io.pinger.groups.config.MessageConfiguration;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.instance.Instances;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    public void unloadGroupIfMatches(Group group) {
        this.assignedGroups.removeIf(assignedGroup -> assignedGroup.getGroup().equals(group));
    }

    public void addAssignedGroups(AssignedGroup... groups) {
        this.addAssignedGroups(Arrays.asList(groups));
    }

    public void addAssignedGroups(Collection<AssignedGroup> groups) {
        this.assignedGroups.addAll(groups);
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

    public void sendMessage(String key) {
        final MessageConfiguration messages = Instances.get(MessageConfiguration.class);
        if (this.toPlayer() == null || !messages.has(key)) {
            return;
        }

        // Get the message
        final String message = messages.of(key);
        this.toPlayer().sendMessage(message);
    }

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
