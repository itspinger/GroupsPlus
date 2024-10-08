package io.pinger.groups.commands.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.Group;
import io.pinger.groups.user.User;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserProvider extends DrinkProvider<User> {
    private final GroupsPlus groupsPlus;

    public UserProvider(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public User provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        final Player player = Bukkit.getPlayer(arg.get());
        if (player == null) {
            throw new CommandExitMessage("No player with name " + arg.getSenderAsPlayer().getName());
        }

        final User user = this.groupsPlus.getUserManager().getUser(player);
        if (user == null) {
            throw new CommandExitMessage("No player with name " + arg.getSenderAsPlayer().getName());
        }

        return user;
    }

    @Override
    public String argumentDescription() {
        return "user";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        final List<String> suggestions = new ArrayList<>();

        // Filter the plugin list to list of plugin names
        final List<String> plugins = this.groupsPlus.getUserManager()
            .getUsers()
            .values()
            .stream()
            .map(User::toPlayer)
            .filter(Objects::nonNull)
            .map(Player::getName)
            .toList();

        // Get all suggestions
        StringUtil.copyPartialMatches(prefix, plugins, suggestions);
        Collections.sort(suggestions);

        // Return the list
        return suggestions;
    }
}