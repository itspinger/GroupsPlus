package io.pinger.groups.timer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final long milliseconds;
    private final TimeUnit truncateTo;
    private final List<String> format;

    private long realMillis;

    public Timer(Builder builder) {
        this.milliseconds = builder.milliseconds;
        this.truncateTo = builder.truncateTo;
        this.realMillis = builder.milliseconds;
        this.format = builder.format;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getMilliseconds() {
        return this.milliseconds;
    }

    public String timeToString() {
        int counter = 0;
        final StringBuilder result = new StringBuilder();

        this.append(result, this.negateAmount(TimeUnit.MILLISECONDS.toDays(this.realMillis), TimeUnit.DAYS), TimeUnit.DAYS, counter++);
        this.append(result, this.negateAmount(TimeUnit.MILLISECONDS.toHours(this.realMillis), TimeUnit.HOURS), TimeUnit.HOURS, counter++);
        this.append(result, this.negateAmount(TimeUnit.MILLISECONDS.toMinutes(this.realMillis), TimeUnit.MINUTES), TimeUnit.MINUTES, counter++);
        this.append(result, this.negateAmount(TimeUnit.MILLISECONDS.toSeconds(this.realMillis), TimeUnit.SECONDS), TimeUnit.SECONDS, counter++);
        this.append(result, this.negateAmount(TimeUnit.MILLISECONDS.toMillis(this.realMillis), TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS, counter);

        if (result.isEmpty()) {
            return "now";
        }

        return result.toString();
    }

    private void append(StringBuilder result, long amount, TimeUnit time, int counter) {
        if (amount != 0 && this.truncateTo.ordinal() <= time.ordinal()) {
            result.append(result.isEmpty() ? "" : ", ").append(amount).append(this.getSuffix(counter));
        }
    }

    private long negateAmount(long amount, TimeUnit type) {
        this.realMillis -= type.toMillis(amount);
        return amount;
    }

    public String getSuffix(int count) {
        return this.format.size() > count ? this.format.get(count) : "";
    }

    public static class Builder {
        private TimeUnit truncateTo = TimeUnit.SECONDS;
        private List<String> format = Format.SHORT.getUnits();
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

        public Builder truncateTo(TimeUnit truncateTo) {
            this.truncateTo = truncateTo;
            return this;
        }

        public Builder format(List<String> format) {
            this.format = format;
            return this;
        }

        public Timer build() {
            return new Timer(this);
        }
    }

    public enum Format {
        SHORT(List.of("d", "h", "m", "s")),
        SHORT_WITH_MILLIS(List.of("d", "h", "m", "s", "ms")),
        LONG(List.of(" days", " hours", " minutes", " seconds")),
        LONG_WITH_MILLIS(List.of(" days", " hours", " minutes", " seconds", " milliseconds"));

        private final List<String> units;

        Format(List<String> units) {
            this.units = units;
        }

        public List<String> getUnits() {
            return this.units;
        }
    }

}
