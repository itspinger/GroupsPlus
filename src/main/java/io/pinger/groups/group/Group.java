package io.pinger.groups.group;

public class Group {
    private final String name;

    private String prefix;
    private long priority;

    public Group(String name) {
        this.name = name;
        this.prefix = name;
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
}
