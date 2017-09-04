package org.opensingular.lib.commons.context;


import org.opensingular.lib.commons.context.singleton.ThreadBoundedSingletonStrategy;

import javax.annotation.Nonnull;

class SingularContextImpl extends DelegationSingletonStrategy implements SingularContext, SingularContextSetup  {

    private static SingularSingletonStrategy strategy;

    private SingularContextImpl() {
        this(new ThreadBoundedSingletonStrategy());
    }

    private SingularContextImpl(SingularSingletonStrategy strategy) {
        if (SingularContextImpl.strategy != null) {
            throw new SingularContextAlreadyConfiguredException();
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
        SingularSingletonStrategy strategy2 = strategy;
        if (strategy2 instanceof ResetEnabledSingularSingletonStrategy) {
            ((ResetEnabledSingularSingletonStrategy) strategy2).reset();
        }
        SingularContextImpl.strategy = null;
    }

    @Nonnull
    @Override
    protected SingularSingletonStrategy getStrategyImpl() {
        return SingularContextImpl.strategy;
    }
}
