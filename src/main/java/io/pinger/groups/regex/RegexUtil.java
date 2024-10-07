package io.pinger.groups.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class RegexUtil {

    public static List<String> extractMatches(@NotNull String input, @NotNull Pattern pattern) {
        final List<String> result = new ArrayList<>();
        final Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

}
