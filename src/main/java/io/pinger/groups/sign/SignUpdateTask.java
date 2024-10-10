package io.pinger.groups.sign;

import io.pinger.groups.GroupsPlus;
import io.pinger.groups.user.User;
import io.pinger.groups.util.Signs;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;

public class SignUpdateTask implements Runnable {
    private final GroupsPlus groupsPlus;
    private final SignManager signManager;

    public SignUpdateTask(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
        this.signManager = groupsPlus.getSignManager();
    }

    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final User user = this.groupsPlus.getUserManager().getUser(player);
            if (user == null) {
                continue;
            }

            final Location location = player.getLocation();
            final List<Sign> signs = Signs.getNearbySignsWithServerRadius(location);
            for (final Sign sign : signs) {
                this.processSign(user, sign);
            }
        }
    }

    private void processSign(User user, Sign sign) {
        for (final Side side : Side.values()) {
            final SignSide signSide = sign.getSide(side);
            final String[] lines = signSide.getLines();

            boolean signModified = false;

            for (int i = 0; i < signSide.getLines().length; i++) {
                final String line = lines[i];
                if (this.signManager.shouldReplaceLine(line)) {
                    signSide.setLine(i, this.signManager.replaceLine(user, line));
                    signModified = true;
                }
            }

            if (signModified) {
                user.toPlayer().sendBlockUpdate(sign.getLocation(), sign);
            }
        }
    }
}
