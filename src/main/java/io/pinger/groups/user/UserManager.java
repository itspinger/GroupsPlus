package io.pinger.groups.user;

import io.pinger.groups.GroupsPlus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UserManager {
    private final Map<UUID, User> users;

    public UserManager(GroupsPlus groupsPlus) {
        this.users = Collections.synchronizedMap(new HashMap<>());

        for (final Player p : Bukkit.getOnlinePlayers()) {
            final User user;
            if (groupsPlus.isDatabaseEnabled()) {
                user = groupsPlus.getStorage().loadUser(p.getUniqueId()).join();
            } else {
                user = new User(p.getUniqueId());
            }

            this.users.put(p.getUniqueId(), user);
        }
    }

    public User getUser(Player player) {
        return this.users.get(player.getUniqueId());
    }

    public User getUser(UUID id) {
        return this.users.get(id);
    }

    public Map<UUID, User> getUsers() {
        return this.users;
    }
}
