package org.opensingular.lib.commons.context.singleton;

import org.opensingular.lib.commons.context.SingularSingletonNotFoundException;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple strategy meant to be used where static variable singletons
 * are required, like standalone multithreaded applications.
 * <p/>
 * In other cases is preferable to use the {@link ThreadBoundedSingletonStrategy}
 * which bounds singletons to the execution thread.
 */
public class InstanceBoundedSingletonStrategy implements SingularSingletonStrategy {

    private final Map<Object, Object> map = new HashMap<>(0);


    @Override
    public synchronized <T> void put(@Nonnull T thisInstance) {
        if (thisInstance != null) {
            map.put(thisInstance.getClass(), thisInstance);
        }
    }

    @Override
    public synchronized <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        if (thisInstance != null) {
            map.put(instanceClazz, thisInstance);
        }
    }

    @Override
    public synchronized <T> void put(String nameKey, T thisInstance) {
        if (thisInstance != null) {
            map.put(nameKey, thisInstance);
        }
    }

    @Override
    public synchronized <T> boolean exists(Class<T> classKey) {
        return map.containsKey(classKey);
    }

    @Override
    public synchronized boolean exists(String nameKey) {
        return map.containsKey(nameKey);
    }

    @Override
    public synchronized <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        T value = (T) map.get(singletonClass);
        if (value == null) {
            throw new SingularSingletonNotFoundException(singletonClass.getName());
        }
        return value;
    }

    @Override
    public synchronized <T> T get(String name) throws SingularSingletonNotFoundException {
        T value = (T) map.get(name);
        if (value == null) {
            throw new SingularSingletonNotFoundException(name);
        }
        return value;
    }

    @Override
    public synchronized <T> T singletonize(@Nonnull String nameKey, @Nonnull Supplier<T> singletonFactory) {
        return (T) map.computeIfAbsent(nameKey, k -> singletonFactory.get());
    }

    @Override
    public synchronized <T> T singletonize(@Nonnull Class<T> classKey, @Nonnull Supplier<T> singletonFactory) {
        Object v = map.computeIfAbsent(classKey, k -> singletonFactory.get());
        return classKey.cast(v);
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        return map;
    }

    @Override
    public synchronized void putEntries(@Nonnull SingularSingletonStrategy source) {
        map.putAll(source.getEntries());
    }
}
