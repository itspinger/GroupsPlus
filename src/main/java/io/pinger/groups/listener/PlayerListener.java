package io.pinger.groups.listener;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.user.User;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        event.
    }

}
