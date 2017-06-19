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
            throw new SingularContextAlreadyConfiguredException();
        } else {
            setSingularSingletonStrategy(strategy);
        }
        strategy.put(SingularContext.class, this);
    }

    synchronized static boolean isConfigured() {
        return getSingularSingletonStrategy() != null;
    }

    private static SingularSingletonStrategy getSingularSingletonStrategy() {
        return strategy;
    }

    private static void setSingularSingletonStrategy(SingularSingletonStrategy strategy) {
        SingularContextImpl.strategy = strategy;
    }

    static synchronized SingularContext get() {
        if (!isConfigured()) {
            setup();
        }
        return getSingularSingletonStrategy().get(SingularContext.class);
    }

    synchronized static void setup(SingularSingletonStrategy singularSingletonStrategy) {
        new SingularContextImpl(singularSingletonStrategy);
    }

    synchronized static void setup() {
        new SingularContextImpl();
    }

    synchronized static void reset() {
        SingularSingletonStrategy strategy = getSingularSingletonStrategy();
        if (strategy instanceof ResetEnabledSingularSingletonStrategy) {
            ((ResetEnabledSingularSingletonStrategy) strategy).reset();
        }
        SingularContextImpl.setSingularSingletonStrategy(null);
    }

    @Override
    public <T> void put(T thisInstance) {
        getSingularSingletonStrategy().put(thisInstance);
    }

    @Override
    public <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        getSingularSingletonStrategy().put(instanceClazz, thisInstance);
    }

    @Override
    public <T> void put(String nameKey, T thisInstance) {
        getSingularSingletonStrategy().put(nameKey, thisInstance);
    }

    @Override
    public <T> boolean exists(Class<T> classKey) {
        return getSingularSingletonStrategy().exists(classKey);
    }

    @Override
    public boolean exists(String nameKey) {
        return getSingularSingletonStrategy().exists(nameKey);
    }

    @Override
    public <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        return getSingularSingletonStrategy().get(singletonClass);
    }

    @Override
    public <T> T get(String name) throws SingularSingletonNotFoundException {
        return getSingularSingletonStrategy().get(name);
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        if (isConfigured() && getSingularSingletonStrategy() instanceof MigrationEnabledSingularSingletonStrategy) {
            return ((MigrationEnabledSingularSingletonStrategy) getSingularSingletonStrategy()).getEntries();
        } else {
            return Collections.emptyMap();
        }

    }

    @Override
    public synchronized void putEntries(Map<Object, Object> entries) {
        if (isConfigured() && getSingularSingletonStrategy() instanceof MigrationEnabledSingularSingletonStrategy) {
            ((MigrationEnabledSingularSingletonStrategy) getSingularSingletonStrategy()).putEntries(entries);
        }
    }
}
