package io.pinger.groups.processor;

import java.util.function.Function;

@FunctionalInterface
public interface Processor<T> extends Function<T, T> {
}
