package io.pinger.groups.listener;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.AssignedGroup;
import io.pinger.groups.group.Group;
import io.pinger.groups.group.GroupPriorityComparator;
import io.pinger.groups.logger.PluginLogger;
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
    private final PluginLogger logger;

    public PlayerListener(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
        this.logger = groupsPlus.getPluginLogger();
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();
        if (!this.groupsPlus.isDatabaseEnabled()) {
            this.groupsPlus.getUserManager().getUsers().put(uuid, new User(uuid));
            return;
        }

        final User user = this.groupsPlus.getStorage().loadUser(uuid).join();
        if (user == null) {
            return;
        }

        final Group defaultGroup = this.groupsPlus.getGroupRepository().getDefaultGroup();
        if (defaultGroup == null) {
            return;
        }

        if (!user.hasGroup(defaultGroup)) {
            try {
                this.groupsPlus.getStorage().addGroupToUser(user, defaultGroup, -1).get();
            } catch (Exception e) {
                this.logger.error("Failed to add default group to user ", e);
            }
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

        format = format
            .replace("{player}", player.getName())
            .replace("{highest_group_prefix}", user.getHighestGroupPrefix(""));

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

        format = format
            .replace("{player}", player.getName())
            .replace("{message}", "%2$s")
            .replace("{highest_group_prefix}", user.getHighestGroupPrefix(""));

        event.setFormat(Text.colorize(format));
    }
}
