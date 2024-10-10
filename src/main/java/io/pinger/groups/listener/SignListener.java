package io.pinger.groups.listener;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.sign.SignManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
    private final SignManager signManager;

    public SignListener(GroupsPlus groupsPlus) {
        this.signManager = groupsPlus.getSignManager();
    }

    @EventHandler
    public void onSignUpdate(SignChangeEvent event) {
        final Player player = event.getPlayer();
        final String[] lines = event.getLines();
        for (final String line : lines) {
            if (!this.signManager.shouldReplaceLine(line)) {
                continue;
            }

            if (!player.hasPermission("permission.gp.signs")) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
