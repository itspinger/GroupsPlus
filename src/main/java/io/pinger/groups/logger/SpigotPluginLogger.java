package io.pinger.groups.logger;

import io.pinger.groups.instance.Instances;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SpigotPluginLogger implements PluginLogger {
    private final Logger logger;

    public SpigotPluginLogger(Logger logger) {
        this.logger = logger;

        Instances.register(this);
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(String message) {
        this.logger.warning(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        this.logger.log(Level.WARNING, message, throwable);
    }

    @Override
    public void error(String message) {
        this.logger.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    public void info(String message, Object... args) {
        this.log(Level.INFO, message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        this.log(Level.WARNING, message, args);
    }

    @Override
    public void error(String message, Object... args) {
        this.log(Level.SEVERE, message, args);
    }

    private void log(Level level, String message, Object... args) {
        final String formattedMessage = MessageFormatter.indexPlaceholders(message);
        final Throwable throwable = MessageFormatter.getThrowableCandidate(args);
        if (throwable == null) {
            this.log(level, formattedMessage, null, args);
            return;
        }

        final Object[] params = MessageFormatter.getMessageCandidate(args);
        this.log(level, formattedMessage, throwable, params);
    }

    private void log(Level level, String message, Throwable throwable, Object... args) {
        this.logger.log(this.of(level, message, throwable, args));
    }

    private LogRecord of(Level level, String message, Throwable throwable,  Object... args) {
        final LogRecord r = new LogRecord(level, message);
        r.setParameters(args);
        r.setThrown(throwable);
        r.setLoggerName(this.logger.getName());
        r.setResourceBundleName(this.logger.getResourceBundleName());
        r.setResourceBundle(this.logger.getResourceBundle());
        return r;
    }
}
