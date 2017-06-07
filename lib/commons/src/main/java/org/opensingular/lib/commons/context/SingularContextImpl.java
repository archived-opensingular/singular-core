package org.opensingular.lib.commons.context;


import org.opensingular.lib.commons.context.singleton.ThreadBoundedSingletonStrategy;

import java.util.Collections;
import java.util.Map;

class SingularContextImpl implements SingularContext, SingularContextSetup, SingularSingletonStrategy, MigrationEnabledSingularSingletonStrategy {

    private static SingularSingletonStrategy strategy;

    private SingularContextImpl() {
        this(new ThreadBoundedSingletonStrategy());
    }

    private SingularContextImpl(SingularSingletonStrategy strategy) {
        if (isConfigured()) {
            throw new SingularAlreadyConfiguredException();
        } else {
            SingularContextImpl.strategy = strategy;
        }
        strategy.put(SingularContext.class, this);
    }

    synchronized static boolean isConfigured() {
        return strategy != null;
    }


    static synchronized SingularContext get() {
        if (!isConfigured()) {
            setup();
        }
        return strategy.get(SingularContext.class);
    }

    synchronized static void setup(SingularSingletonStrategy singularSingletonStrategy) {
        new SingularContextImpl(singularSingletonStrategy);
    }

    synchronized static void setup() {
        new SingularContextImpl();
    }

    synchronized static void reset() {
        SingularContextImpl.strategy = null;
    }

    @Override
    public <T> void put(T thisInstance) {
        strategy.put(thisInstance);
    }

    @Override
    public <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        strategy.put(instanceClazz, thisInstance);
    }

    @Override
    public <T> void put(String nameKey, T thisInstance) {
        strategy.put(nameKey, thisInstance);
    }

    @Override
    public <T> boolean exists(Class<T> classKey) {
        return strategy.exists(classKey);
    }

    @Override
    public boolean exists(String nameKey) {
        return strategy.exists(nameKey);
    }

    @Override
    public <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        return strategy.get(singletonClass);
    }

    @Override
    public <T> T get(String name) throws SingularSingletonNotFoundException {
        return strategy.get(name);
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        if (isConfigured() && strategy instanceof MigrationEnabledSingularSingletonStrategy) {
            return ((MigrationEnabledSingularSingletonStrategy) strategy).getEntries();
        } else {
            return Collections.emptyMap();
        }

    }

    @Override
    public synchronized void putEntries(Map<Object, Object> entries) {
        if (isConfigured() && strategy instanceof MigrationEnabledSingularSingletonStrategy) {
            ((MigrationEnabledSingularSingletonStrategy) strategy).putEntries(entries);
        }
    }
}
