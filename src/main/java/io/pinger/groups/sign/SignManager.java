package io.pinger.groups.sign;

import io.pinger.groups.sign.processors.PlayerGroupProcessor;
import io.pinger.groups.sign.processors.PlayerNameProcessor;
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

}
