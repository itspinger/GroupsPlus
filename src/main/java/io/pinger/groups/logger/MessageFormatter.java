package io.pinger.groups.logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageFormatter {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{}");

    /**
     * Extracts the throwable candidate from the provided arguments.
     *
     * @param args the array of arguments from which to extract the throwable.
     * @return the throwable if it is the last element of the array; otherwise, returns {@code null}.
     */
    public static @Nullable Throwable getThrowableCandidate(@Nullable Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        final Object throwable = args[args.length - 1];
        return throwable instanceof Throwable ? (Throwable) throwable : null;
    }

    /**
     * Replaces `{}` placeholders in the given message with indexed placeholders `{0}`, `{1}`, etc.
     *
     * @param message the message containing `{}` placeholders to be replaced.
     * @return the message with `{}` placeholders replaced by `{0}`, `{1}`, etc.
     */
    public static @NotNull String indexPlaceholders(@NotNull String message) {
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        final StringBuilder buffer = new StringBuilder();

        int index = 0;

        while (matcher.find()) {
            matcher.appendReplacement(buffer, "{" + index++ + "}");
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Extracts the message candidates from the provided arguments, excluding the last element which is assumed
     * to be a throwable.
     *
     * @param args the array of arguments from which to extract the message candidates.
     * @return a new array containing all elements except the last one; returns {@code null} if the input is
     *         {@code null} or empty.
     */
    public static @Nullable Object[] getMessageCandidate(@Nullable Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        final int newSize = args.length - 1;
        final Object[] newArgs = new Object[newSize];
        if (newSize == 0) {
            return newArgs;
        }

        System.arraycopy(args, 0, newArgs, 0, newSize);
        return newArgs;
    }

}
