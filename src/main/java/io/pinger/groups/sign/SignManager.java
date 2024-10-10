package io.pinger.groups.sign;

import io.pinger.groups.user.User;
import io.pinger.groups.util.Text;
import java.util.Arrays;
import java.util.List;

public class SignManager {
    private final List<SignProcessor> processors;

    public SignManager() {
        this.processors = Arrays.asList(new PlayerGroupProcessor(), new PlayerNameProcessor());
    }

    public boolean shouldReplaceLine(String line) {
        for (final SignProcessor processor : this.processors) {
            if (processor.shouldReplaceLine(line)) {
                return true;
            }
        }
        return false;
    }

    public String replaceLine(User user, String line) {
        String newLine = line;
        boolean modified = false;
        for (final SignProcessor processor : this.processors) {
            if (!processor.shouldReplaceLine(newLine)) {
                continue;
            }

            newLine = processor.replaceLine(user, newLine);
            modified = true;
        }

        if (modified) {
            newLine = Text.colorize(newLine);
        }

        return newLine;
    }

    public interface SignProcessor {

        boolean shouldReplaceLine(String line);

        String replaceLine(User user, String line);

    }

    public static class PlayerNameProcessor implements SignProcessor {
        private static final String NAME_PLACEHOLDER = "{name}";

        @Override
        public boolean shouldReplaceLine(String line) {
            return line.contains(NAME_PLACEHOLDER);
        }

        @Override
        public String replaceLine(User user, String line) {
            return line.replace(NAME_PLACEHOLDER, user.toPlayer().getName());
        }
    }

    public static class PlayerGroupProcessor implements SignProcessor {
        private static final String RANK_PLACEHOLDER = "{rank}";

        @Override
        public boolean shouldReplaceLine(String line) {
            return line.contains(RANK_PLACEHOLDER);
        }

        @Override
        public String replaceLine(User user, String line) {
            return line.replace(RANK_PLACEHOLDER, user.getHighestGroupPrefix("&e"));
        }
    }
}
