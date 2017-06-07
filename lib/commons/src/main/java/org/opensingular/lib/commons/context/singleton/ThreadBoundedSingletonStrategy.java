package org.opensingular.lib.commons.context.singleton;



import org.opensingular.lib.commons.context.MigrationEnabledSingularSingletonStrategy;
import org.opensingular.lib.commons.context.SingularSingletonNotFoundException;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

import java.util.HashMap;
import java.util.Map;

public class ThreadBoundedSingletonStrategy implements SingularSingletonStrategy, MigrationEnabledSingularSingletonStrategy {

    private static final ThreadLocal<Map<Object, Object>> threadBounded = new ThreadLocal<Map<Object, Object>>() {
        protected Map<Object, Object> initialValue() {
            return new HashMap<>();
        }
    };

    @Override
    public <T> void put(T thisInstance) {
        threadBounded.get().put(thisInstance.getClass(), thisInstance);
    }

    @Override
    public <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        threadBounded.get().put(instanceClazz, thisInstance);
    }

    @Override
    public <T> void put(String nameKey, T thisInstance) {
        threadBounded.get().put(nameKey, thisInstance);
    }

    @Override
    public <T> boolean exists(Class<T> classKey) {
        return threadBounded.get().containsKey(classKey);
    }

    @Override
    public boolean exists(String nameKey) {
        return threadBounded.get().containsKey(nameKey);
    }

    @Override
    public <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        T value = (T) threadBounded.get().get(singletonClass);
        if (value == null) {
            throw new SingularSingletonNotFoundException(singletonClass.getName());
        }
        return value;
    }

    @Override
    public <T> T get(String name) throws SingularSingletonNotFoundException {
        T value = (T) threadBounded.get().get(name);
        if (value == null) {
            throw new SingularSingletonNotFoundException(name);
        }
        return value;
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        return threadBounded.get();
    }

    @Override
    public synchronized void putEntries(Map<Object, Object> entries) {
        threadBounded.get().putAll(entries);
    }
}
