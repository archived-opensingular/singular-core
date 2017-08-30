package org.opensingular.lib.commons.context.singleton;


import org.opensingular.lib.commons.context.DelegationSingletonStrategy;
import org.opensingular.lib.commons.context.ResetEnabledSingularSingletonStrategy;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

import javax.annotation.Nonnull;

public class ThreadBoundedSingletonStrategy extends DelegationSingletonStrategy implements ResetEnabledSingularSingletonStrategy {

    private static final ThreadLocal<InstanceBoundedSingletonStrategy> threadBounded = new ThreadLocal<InstanceBoundedSingletonStrategy>() {

        @Override
        protected InstanceBoundedSingletonStrategy initialValue() {
            return new InstanceBoundedSingletonStrategy();
        }
    };

    @Nonnull
    @Override
    protected SingularSingletonStrategy getStrategyImpl() {
        return threadBounded.get();
    }

    @Override
    public void reset() {
        threadBounded.remove();
    }
}
