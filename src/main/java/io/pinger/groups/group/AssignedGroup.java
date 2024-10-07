package io.pinger.groups.group;

import io.pinger.groups.timer.Timer;
import io.pinger.groups.user.User;

public class AssignedGroup {
    private final User user; // The user the group was assigned to
    private final Group group; // The group that was assigned
    private final long expiresAt; // The time when the group expired (-1 if it's permanent)

    public AssignedGroup(User user, Group group, long expiry) {
        this.user = user;
        this.group = group;
        this.expiresAt = expiry;
    }

    public AssignedGroup(User user, Group group) {
        this(user, group, -1);
    }

    public boolean isPermanent() {
        return this.expiresAt == -1;
    }

    public boolean isTemporary() {
        return !this.isPermanent();
    }

    public boolean isActive() {
        return !this.isExpired();
    }

    public boolean isExpired() {
        return this.isTemporary() && System.currentTimeMillis() > this.expiresAt;
    }

    public Timer getTimeLeft() {
        return Timer.builder().timeTo(this.expiresAt).build();
    }

    public User getUser() {
        return this.user;
    }

    public Group getGroup() {
        return this.group;
    }
}
