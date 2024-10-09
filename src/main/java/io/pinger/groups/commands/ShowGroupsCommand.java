package io.pinger.groups.commands;

import static io.pinger.groups.util.Text.colorize;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.GroupPriorityComparator;
import io.pinger.groups.timer.Timer;
import io.pinger.groups.user.User;
import java.util.List;
import org.bukkit.entity.Player;

public class ShowGroupsCommand {
    private final GroupsPlus groupsPlus;

    public ShowGroupsCommand(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
    }

    @Command(name = "", desc = "Show the groups this user has")
    public void showGroups(@Sender Player sender) {
        final User user = this.groupsPlus.getUserManager().getUser(sender);
        if (user == null) {
            return;
        }

        final List<AssignedGroup> groups = user.getActiveAssignedGroups().stream()
            .sorted((a, b) -> new GroupPriorityComparator().compare(a.getGroup(), b.getGroup()))
            .toList();

        sender.sendMessage(colorize("&f&m--------------------------------"));
        sender.sendMessage(colorize("&aYour Groups: "));

        for (final AssignedGroup group : groups) {
            final String groupName = group.getGroup().getName();
            if (group.isTemporary()) {
                final Timer timeLeft = group.getTimeLeft();
                sender.sendMessage(colorize(String.format("&a❙ &e%s &f(&b%s&f)", groupName, timeLeft.timeToString())));
            } else {
                sender.sendMessage(colorize(String.format("&a❙ &e%s", groupName)));
            }
        }

        sender.sendMessage(colorize("&f&m--------------------------------"));
    }

}
