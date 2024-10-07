package io.pinger.groups.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class TimeUtilTest {

    @Test
    public void testParseTimeIntoSeconds() {
        assertEquals(this.parse(5, 4), TimeUtil.parseTimeIntoSeconds("5m4s"));
        assertEquals(this.parse(2, 26, 59), TimeUtil.parseTimeIntoSeconds("2h26m59s"));
        assertEquals(this.parse(4, 0, 7, 23), TimeUtil.parseTimeIntoSeconds("4d7m23s"));
        assertEquals(this.parse(4, 0, 7, 23), TimeUtil.parseTimeIntoSeconds("4d 7m23s"));
        assertEquals(this.parse(4, 0, 7, 23), TimeUtil.parseTimeIntoSeconds("4d 7m 23s"));
        assertEquals(this.parse(4, 0, 7, 23), TimeUtil.parseTimeIntoSeconds("4d 7m  23s"));
        assertEquals(this.parse(4, 0, 7, 23), TimeUtil.parseTimeIntoSeconds("  4d  7m  23s"));
    }

    private long parse(long minutes, long seconds) {
        return this.parse(0, minutes, seconds);
    }

    private long parse(long hours, long minutes, long seconds) {
        return this.parse(0, hours, minutes, seconds);
    }

    private long parse(long days, long hours, long minutes, long seconds) {
        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds).getSeconds();
    }

}
