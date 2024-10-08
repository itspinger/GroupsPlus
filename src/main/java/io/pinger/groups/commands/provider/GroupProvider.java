package io.pinger.groups.commands.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import io.pinger.groups.GroupsPlus;
import io.pinger.groups.group.Group;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupProvider extends DrinkProvider<Group> {
    private final GroupsPlus groupsPlus;

    public GroupProvider(GroupsPlus groupsPlus) {
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

    @Override
    public @Nullable Group provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        final String groupName = arg.get();
        final Group group = this.groupsPlus.getGroupRepository().findGroupByName(groupName);
        if (group == null) {
            throw new CommandExitMessage("Unknown group " + groupName);
        }

        return group;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, @NotNull String prefix) {
        if (!sender.hasPermission("permission.gp.listgroups")) {
            return new ArrayList<>();
        }

        final List<String> suggestions = new ArrayList<>();

        // Filter the plugin list to list of plugin names
        final List<String> plugins = this.groupsPlus.getGroupRepository()
            .getAllGroups()
            .stream()
            .map(Group::getName)
            .toList();

        // Get all suggestions
        StringUtil.copyPartialMatches(prefix, plugins, suggestions);
        Collections.sort(suggestions);

        // Return the list
        return suggestions;
    }

    @Override
    public String argumentDescription() {
        return "group";
    }
}
