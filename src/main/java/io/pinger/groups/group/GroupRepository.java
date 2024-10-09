package io.pinger.groups.group;

import io.pinger.groups.GroupsPlus;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class GroupRepository {
    private final GroupsPlus groupsPlus;
    private final Map<String, Group> groups;

    private Group defaultGroup;

    public GroupRepository(GroupsPlus groupsPlus) {
        this.groupsPlus = groupsPlus;
        this.groups = Collections.synchronizedMap(new HashMap<>());
    }

    public void overrideGroups(Map<String, Group> newGroups) {
        this.groups.clear();
        this.groups.putAll(newGroups);
    }

    public Collection<Group> getAllGroups() {
        return this.groups.values();
    }

    public void addGroup(Group group) {
        this.groups.put(group.getName(), group);
    }

    public void unloadGroup(Group group) {
        this.groups.remove(group.getName());
        this.groupsPlus.getUserManager().unloadGroupFromUsers(group);
    }

    public Group findGroupByName(String name) {
        return this.groups.get(name);
    }

    public void setDefaultGroup(Group defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    @Nullable
    public Group getDefaultGroup() {
        return this.defaultGroup;
    }
}
