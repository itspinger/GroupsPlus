package io.pinger.groups.group;

import java.util.Comparator;

public class GroupPriorityComparator implements Comparator<Group> {

    @Override
    public int compare(Group a, Group b) {
        return Long.compare(a.getPriority(), b.getPriority());
    }
}
