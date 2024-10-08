package io.pinger.groups.util;

import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class Text {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static final char COLOR_CHAR = '§';

    public static @NotNull String colorize(@NotNull String text) {
        return Text.colorize('&', Text.COLOR_CHAR, text);
    }

    public static @NotNull String colorize(char altColorChar, char newColorChar, @NotNull String text) {
        final char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = newColorChar;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
