package io.pinger.groups.group;

import io.pinger.groups.GroupsPlus;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroupRepository {
    private final GroupsPlus groupsPlus;
    private final Map<String, Group> groups;

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

    public Group findGroupByName(String name) {
        return this.groups.get(name);
    }

}
