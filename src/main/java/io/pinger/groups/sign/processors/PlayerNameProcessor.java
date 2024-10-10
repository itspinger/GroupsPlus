package io.pinger.groups.sign.processors;

import io.pinger.groups.sign.SignProcessor;
import io.pinger.groups.user.User;

public class PlayerNameProcessor implements SignProcessor {
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
