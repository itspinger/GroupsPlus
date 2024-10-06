package io.pinger.groups.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class Instances {
    private static final Map<Class<?>, Object> INSTANCES = new HashMap<>();

    private Instances() {}

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return (T) Instances.INSTANCES.get(clazz);
    }

    public static <T> T getOrThrow(Class<T> clazz) {
        final T result = Instances.get(clazz);
        if (result == null) {
            throw new IllegalStateException("Instances bound for class " + clazz.getSimpleName() + " was not found!");
        }
        return result;
    }

    public static void register(@NotNull Object instance) {
        final List<Class<?>> classes = Instances.getAllInterfaces(instance.getClass());
        for (final Class<?> clazz : classes) {
            if (Instances.INSTANCES.containsKey(clazz)) {
                throw new IllegalStateException("Duplicate instance found for " + clazz);
            }

            Instances.INSTANCES.put(clazz, instance);
        }
    }

    private static List<Class<?>> getAllInterfaces(@NotNull Class<?> clazz) {
        final Set<Class<?>> interfaces = new HashSet<>();
        while (clazz != null && !clazz.equals(Object.class)) {
            interfaces.add(clazz);
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        }
        return new ArrayList<>(interfaces);
    }

}
