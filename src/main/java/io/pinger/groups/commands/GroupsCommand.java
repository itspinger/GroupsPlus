package io.pinger.groups.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import io.pinger.groups.GroupsPlus;
import io.pinger.groups.config.MessageConfiguration;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.logger.PluginLogger;
import io.pinger.groups.timer.Timer;
import io.pinger.groups.user.User;
import java.util.List;
import org.bukkit.command.CommandSender;

public class GroupsCommand {
    private final GroupsPlus groupsPlus;
    private final PluginLogger logger;
    private final MessageConfiguration messagesYml;

    public GroupsCommand(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
        this.logger = groupsPlus.getPluginLogger();
        this.messagesYml = groupsPlus.getMessageConfiguration();
    }

    @Require("permission.gp.addgroup")
    @Command(name = "add", desc = "Add group to a user", usage = "<player> <group> [time]")
    public void addGroupToPlayer(@Sender CommandSender sender, User target, Group group, @OptArg Timer timer) {
        final long expiresAt = timer == null ? -1 : System.currentTimeMillis() + timer.getMilliseconds();
        final List<AssignedGroup> groups = target.getActiveAssignedGroups();
        final AssignedGroup matchingGroup = this.findMatchingGroup(groups, group);
        if (matchingGroup != null) {
            this.messagesYml.sendMessage(sender, "group.already-active");
            return;
        }

        try {
            this.groupsPlus.getStorage().addGroupToUser(target, group, expiresAt).get();
        } catch (Exception e) {
            this.messagesYml.sendMessage(sender, "group.failed-to-assign");
            this.logger.error("Failed to add group {} to user {} {}", group.getName(), target.getId(), e);
            return;
        }

        final String playerName = target.toPlayer().getName();
        final String timerToString = timer == null ? "permanent" : timer.timeToString();
        this.messagesYml.sendMessage(sender, "group.assign-success", group.getName(), playerName, timerToString);

        if (!target.toPlayer().equals(sender)) {
            target.sendMessage("group.assign-success-target", group.getName(), timerToString);
        }
    }

    @Require("permission.dp.removegroup")
    @Command(name = "remove", desc = "Remove group from a user", usage = "<player> <group>")
    public void removeGroupFromPlayer(@Sender CommandSender sender, User target, Group group) {
        final List<AssignedGroup> groups = target.getActiveAssignedGroups();
        final AssignedGroup matchingGroup = this.findMatchingGroup(groups, group);
        if (matchingGroup == null) {
            this.messagesYml.sendMessage(sender, "group.not-active");
            return;
        }

        try {
            this.groupsPlus.getStorage().removeGroupFromUser(target, matchingGroup).get();
        } catch (Exception e) {
            this.messagesYml.sendMessage(sender, "group.failed-to-remove");
            this.logger.error("Failed to remove group {} from user {} {}", group.getName(), target.getId(), e);
            return;
        }

        final String playerName = target.toPlayer().getName();
        this.messagesYml.sendMessage(sender, "group.remove-success", group.getName(), playerName);

        if (!target.toPlayer().equals(sender)) {
            target.sendMessage("group.remove-success-target", group.getName());
        }
    }

    private AssignedGroup findMatchingGroup(List<AssignedGroup> groups, Group newGroup) {
        return groups.stream().filter((group) -> group.getGroup().equals(newGroup)).findFirst().orElse(null);
    }

}
