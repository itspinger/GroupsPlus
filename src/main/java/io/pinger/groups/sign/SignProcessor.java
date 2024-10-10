package io.pinger.groups.sign;

import io.pinger.groups.user.User;

public interface SignProcessor {

    boolean shouldReplaceLine(String line);

    String replaceLine(User user, String line);

}
