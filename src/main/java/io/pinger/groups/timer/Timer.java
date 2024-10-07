package io.pinger.groups.timer;

public class Timer {
    private final long milliseconds;

    public Timer(Builder builder) {
        this.milliseconds = builder.milliseconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getMilliseconds() {
        return this.milliseconds;
    }

    public static class Builder {
        private long milliseconds;

        public Builder withMilliseconds(long milliseconds) {
            this.milliseconds = milliseconds;
            return this;
        }

        public Builder timeSince(long milliseconds) {
            this.milliseconds = System.currentTimeMillis() - milliseconds;
            return this;
        }

        public Builder timeTo(long milliseconds) {
            this.milliseconds = milliseconds - System.currentTimeMillis();
            return this;
        }

        public Timer build() {
            return new Timer(this);
        }
    }

}
