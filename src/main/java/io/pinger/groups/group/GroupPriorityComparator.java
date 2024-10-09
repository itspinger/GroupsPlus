package io.pinger.groups.group;

import java.util.Comparator;

public class GroupPriorityComparator implements Comparator<AssignedGroup> {

    @Override
    public int compare(AssignedGroup a, AssignedGroup b) {
        return Long.compare(b.getPriority(), a.getPriority());
    }
}
