package io.pinger.groups.listener;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.GroupPriorityComparator;
import io.pinger.groups.user.User;
import io.pinger.groups.util.Text;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final GroupsPlus groupsPlus;

    public PlayerListener(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();
        final User user;
        if (!this.groupsPlus.isDatabaseEnabled()) {
            user = new User(uuid);
        } else {
            user = this.groupsPlus.getStorage().loadUser(uuid).join();
        }

        this.groupsPlus.getUserManager().getUsers().put(uuid, user);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = this.groupsPlus.getUserManager().getUser(player);
        if (user == null) {
            return;
        }

        String format = this.groupsPlus.getConfig().getString("join-message");
        if (format == null) {
            return;
        }

        final AssignedGroup highestGroup = user.getActiveAssignedGroups()
            .stream()
            .min(new GroupPriorityComparator())
            .orElse(null);

        format = format.replace("{player}", player.getName());

        if (highestGroup != null) {
            format = format.replace("{highest_group_prefix}", highestGroup.getGroup().getPrefix());
        }

        event.setJoinMessage(Text.colorize(format));
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final User user = this.groupsPlus.getUserManager().getUser(player);
        if (user == null) {
            return;
        }

        String format = this.groupsPlus.getConfig().getString("chat-format");
        if (format == null) {
            return;
        }

        final AssignedGroup highestGroup = user.getActiveAssignedGroups()
            .stream()
            .min(new GroupPriorityComparator())
            .orElse(null);

        format = format
            .replace("{player}", player.getName())
            .replace("{message}", "%2$s");

        if (highestGroup != null) {
            format = format.replace("{highest_group_prefix}", highestGroup.getGroup().getPrefix());
        }

        event.setFormat(Text.colorize(format));
    }
}
