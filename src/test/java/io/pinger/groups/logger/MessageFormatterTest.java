package io.pinger.groups.logger;

import static io.pinger.groups.logger.MessageFormatter.getMessageCandidate;
import static io.pinger.groups.logger.MessageFormatter.getThrowableCandidate;
import static io.pinger.groups.logger.MessageFormatter.indexPlaceholders;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MessageFormatterTest {

    @Test
    void testIndexedPlaceholders() {
        final String message = "Error trying to find a {} in the {}, {}";
        assertEquals("Error trying to find a {0} in the {1}, {2}", indexPlaceholders(message));
    }

    @Test
    void testMessageCandidate() {
        final Throwable throwable = new Throwable("sdasf");
        final Object object = new Object();
        final Object[] args = new Object[] { object, object, throwable};
        assertArrayEquals(new Object[] {object, object}, getMessageCandidate(args));
        assertEquals(throwable, getThrowableCandidate(args));
    }

}
