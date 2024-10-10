package io.pinger.groups.sign.processors;

import io.pinger.groups.sign.SignProcessor;
import io.pinger.groups.user.User;

public class PlayerGroupProcessor implements SignProcessor {
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
