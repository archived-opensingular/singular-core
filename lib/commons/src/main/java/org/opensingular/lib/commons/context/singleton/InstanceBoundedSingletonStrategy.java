package org.opensingular.lib.commons.context.singleton;

import org.opensingular.lib.commons.context.MigrationEnabledSingularSingletonStrategy;
import org.opensingular.lib.commons.context.SingularSingletonNotFoundException;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.lambda.ISupplier;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple strategy meant to be used where static variable singletons
 * are required, like standalone multithreaded applications.
 * <p/>
 * In other cases is preferable to use the {@link ThreadBoundedSingletonStrategy}
 * which bounds singletons to the execution thread.
 */
public class InstanceBoundedSingletonStrategy implements SingularSingletonStrategy, MigrationEnabledSingularSingletonStrategy {

    private Map<Object, Object> map = new HashMap<>(0);


    @Override
    public synchronized <T> void put(T thisInstance) {
        if (thisInstance != null) {
            map.put(thisInstance.getClass(), thisInstance);
        }
    }

    @Override
    public <T> void put(Class<? super T> instanceClazz, T thisInstance) {
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
    public synchronized <T> T singletonize(String nameKey, ISupplier<T> singletonFactory) {
        return SingularSingletonStrategy.super.singletonize(nameKey, singletonFactory);
    }

    @Override
    public synchronized <T> T singletonize(Class<? super T> classKey, ISupplier<T> singletonFactory) {
        return SingularSingletonStrategy.super.singletonize(classKey, singletonFactory);
    }

    @Override
    public Map<Object, Object> getEntries() {
        return map;
    }

    @Override
    public void putEntries(Map<Object, Object> entries) {
        map.putAll(entries);
    }
}
