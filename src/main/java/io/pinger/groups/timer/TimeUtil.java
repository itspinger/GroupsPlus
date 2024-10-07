package io.pinger.groups.timer;

import com.google.common.primitives.Longs;
import io.pinger.groups.regex.RegexUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class TimeUtil {
    private static final Pattern NUMBERS_PATTERN = Pattern.compile("([0-9]+)");
    private static final Pattern CHARS_PATTERN = Pattern.compile("([smhd])");

    private static final Map<Character, TimeUnit> CHARACTER_TIME_UNIT_MAP = new HashMap<>() {{
        this.put('s', TimeUnit.SECONDS);
        this.put('m', TimeUnit.MINUTES);
        this.put('h', TimeUnit.HOURS);
        this.put('d', TimeUnit.DAYS);
    }};

    public static long parseTimeIntoSeconds(@NotNull String input) {
        final List<String> numbers = RegexUtil.extractMatches(input, TimeUtil.NUMBERS_PATTERN);
        final List<String> chars = RegexUtil.extractMatches(input, TimeUtil.CHARS_PATTERN);

        if (numbers.size() != chars.size()) {
            throw new IllegalStateException(String.format("Invalid time format: %s", input));
        }

        long time = 0;
        for (int i = 0; i < numbers.size(); i++) {
            final long parsed = Longs.tryParse(numbers.get(i));
            final char type = chars.get(i).toCharArray()[0];

            final TimeUnit timeUnit = CHARACTER_TIME_UNIT_MAP.get(type);
            time += timeUnit.toSeconds(parsed);
        }

        return time;
    }

}
