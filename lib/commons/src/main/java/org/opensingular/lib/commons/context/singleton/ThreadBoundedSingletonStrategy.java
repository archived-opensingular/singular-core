package org.opensingular.lib.commons.context.singleton;


import org.opensingular.lib.commons.context.MigrationEnabledSingularSingletonStrategy;
import org.opensingular.lib.commons.context.SingularSingletonNotFoundException;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

import java.util.Map;

public class ThreadBoundedSingletonStrategy implements SingularSingletonStrategy, MigrationEnabledSingularSingletonStrategy {

    private static final ThreadLocal<InstanceBoundedSingletonStrategy> threadBounded = new ThreadLocal<InstanceBoundedSingletonStrategy>() {
        protected InstanceBoundedSingletonStrategy initialValue() {
            return new InstanceBoundedSingletonStrategy();
        }
    };

    @Override
    public <T> void put(T thisInstance) {
        threadBounded.get().put(thisInstance);
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
        return threadBounded.get().exists(classKey);
    }

    @Override
    public boolean exists(String nameKey) {
        return threadBounded.get().exists(nameKey);
    }

    @Override
    public <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        return threadBounded.get().get(singletonClass);
    }

    @Override
    public <T> T get(String name) throws SingularSingletonNotFoundException {
        return threadBounded.get().get(name);
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        return threadBounded.get().getEntries();
    }

    @Override
    public synchronized void putEntries(Map<Object, Object> entries) {
        threadBounded.get().putEntries(entries);
    }
}
