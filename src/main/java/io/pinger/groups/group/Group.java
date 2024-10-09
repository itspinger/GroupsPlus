package io.pinger.groups.group;

import java.util.Objects;

public class Group {
    private final String name;

    private String prefix;
    private long priority;

    public Group(String name) {
        this.name = name;
        this.prefix = "&e[" + name + "]";
        this.priority = 1L;
    }

    public Group(String name, String prefix, long priority) {
        this.name = name;
        this.prefix = prefix;
        this.priority = priority;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getPriority() {
        return this.priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        final Group group = (Group) object;
        return this.priority == group.priority &&
               Objects.equals(this.name, group.name) &&
               Objects.equals(this.prefix, group.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.prefix, this.priority);
    }
}
