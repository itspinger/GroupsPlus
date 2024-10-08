package io.pinger.groups.commands;

import static io.pinger.groups.constraints.GroupConstraints.MAX_GROUP_LENGTH;
import static io.pinger.groups.constraints.GroupConstraints.MAX_GROUP_PREFIX_LENGTH;
import static io.pinger.groups.util.Text.colorize;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import io.pinger.groups.GroupsPlus;
import io.pinger.groups.config.MessageConfiguration;
import io.pinger.groups.group.Group;
import io.pinger.groups.logger.PluginLogger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GroupManagerCommand {
    private final GroupsPlus groupsPlus;
    private final PluginLogger logger;
    private final MessageConfiguration messagesYml;

    public GroupManagerCommand(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
        this.logger = groupsPlus.getPluginLogger();
        this.messagesYml = groupsPlus.getMessageConfiguration();
    }

    @Require("permission.gp.creategroup")
    @Command(name = "creategroup", desc = "Create a group", usage = "<groupName>")
    public void createGroup(@Sender CommandSender sender, @NotNull String name) {
        final Group existingGroup = this.groupsPlus.getGroupRepository().findGroupByName(name);
        if (existingGroup != null) {
            this.messagesYml.sendMessage(sender, "group.exists", name);
            return;
        }

        if (name.isEmpty() || name.length() >= MAX_GROUP_LENGTH) {
            this.messagesYml.sendMessage(sender, "group.invalid-length", name);
            return;
        }

        try {
            this.groupsPlus.getStorage().createNewGroup(name).get();
        } catch (Exception e) {
            this.messagesYml.sendMessage(sender, "group.create-failed");
            this.logger.error("Failed to create group with name {}, error {}", name, e);
            return;
        }

        this.messagesYml.sendMessage(sender, "group.create-success", name);
    }

    @Require("permission.gp.removegroup")
    @Command(name = "deletegroup", desc = "Delete a group", usage = "<group>")
    public void deleteGroup(@Sender CommandSender sender, @NotNull Group group) {
        try {
            this.groupsPlus.getStorage().deleteGroup(group).get();
        } catch (Exception e) {
            this.messagesYml.sendMessage(sender, "group.delete-failed", group.getName());
            this.logger.error("Failed to delete group with name {}, error {}", group.getName(), e);
            return;
        }

        this.messagesYml.sendMessage(sender, "group.delete-success", group.getName());
        this.groupsPlus.getGroupRepository().getAllGroups().forEach(group1 -> System.out.println(group1.getName()));
    }

    @Require("permission.gp.setprefix")
    @Command(name = "prefix", desc = "Set prefix for a group", usage = "<groupName> <prefix>")
    public void updateGroupPrefix(@Sender CommandSender sender, @NotNull Group group, @com.jonahseguin.drink.annotation.Text String prefix) {
        if (group.getPrefix().equals(prefix)) {
            this.messagesYml.sendMessage(sender, "group.prefix-already-set");
            return;
        }

        if (prefix.length() >= MAX_GROUP_PREFIX_LENGTH) {
            this.messagesYml.sendMessage(sender, "group.prefix-invalid-length", prefix);
            return;
        }

        final String oldPrefix = group.getPrefix();
        group.setPrefix(prefix); // Set the new one

        try {
            this.groupsPlus.getStorage().updateGroup(group).get();
        } catch (Exception e) {
            group.setPrefix(oldPrefix); // Transaction (all or nothing)
            this.messagesYml.sendMessage(sender, "group.update-failed", group.getName());
            this.logger.error("Failed to update prefix for a group with name {}, error {}", group.getName(), e);
            return;
        }

        this.messagesYml.sendMessage(sender, "group.update-success", group.getName());
    }


    @Require("permission.gp.setpriority")
    @Command(name = "priority", desc = "Set priority for a group", usage = "<groupName> <priority>")
    public void updateGroupPriority(@Sender CommandSender sender, @NotNull Group group, long priority) {
        if (group.getPriority() == priority) {
            this.messagesYml.sendMessage(sender, "group.priority-already-set");
            return;
        }

        final long oldPriority = group.getPriority();
        group.setPriority(priority);

        try {
            this.groupsPlus.getStorage().updateGroup(group).get();
        } catch (Exception e) {
            group.setPriority(oldPriority); // Transaction (all or nothing)
            this.messagesYml.sendMessage(sender, "group.update-failed", group.getName());
            this.logger.error("Failed to update prefix for a group with name {}, error {}", group.getName(), e);
            return;
        }

        this.messagesYml.sendMessage(sender, "group.update-success", group.getName());
    }

    @Require("permission.gp.groupinfo")
    @Command(name = "info", desc = "Get information about specific group", usage = "<groupName>")
    public void getGroupInfo(@Sender CommandSender sender, @NotNull Group group) {
        sender.sendMessage(colorize("&f&m--------------------------------"));
        sender.sendMessage(colorize("&aGroup Information"));
        sender.sendMessage(colorize("&a❙ &fGroup: &e" + group.getName()));
        sender.sendMessage(colorize("&a❙ &fPrefix: &e" + group.getPrefix()));
        sender.sendMessage(colorize("&a❙ &fPriority: &e" + group.getPriority()));
        sender.sendMessage(colorize("&f&m--------------------------------"));
    }
}
