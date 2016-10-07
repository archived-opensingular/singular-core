/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.function.Function;

import org.opensingular.flow.core.TransitionAccess.TransitionAccessLevel;

public class TransitionAccessStrategyImpl<X extends TaskInstance> implements TransitionAccessStrategy<X>{

    private final Function<X, TransitionAccess> strategyImpl;
    
    private TransitionAccessStrategyImpl(Function<X, TransitionAccess> strategyImpl) {
        super();
        this.strategyImpl = strategyImpl;
    }

    @Override
    public TransitionAccess getAccess(X taskInstance) {
        return strategyImpl.apply(taskInstance);
    }

    public static <T extends TaskInstance> TransitionAccessStrategy<T> of(Function<T, TransitionAccess> strategyImpl) {
        return new TransitionAccessStrategyImpl<>(strategyImpl);
    }

    public static <T extends TaskInstance> TransitionAccessStrategy<T> enabled(boolean enabled) {
        return (instance) -> {
            if (enabled) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED, null);
            } else {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_AND_HIDDEN, "Unauthorized action");
            }
        };
    }

    public static <T extends TaskInstance> TransitionAccessStrategy<T> sameStrategyOf(final MTask<?> task, boolean visible) {
        return (instance) -> {
            MUser user = Flow.getUserIfAvailable();
            boolean canExecute = user != null && task.getAccessStrategy().canExecute(instance, user);
            if (canExecute && visible) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED, null);
            } else if (canExecute && !visible) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED_BUT_HIDDEN, null);
            } else if (!canExecute && visible) {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_BUT_VISIBLE, "Unauthorized action");
            } else {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_AND_HIDDEN, "Unauthorized action");
            }
        };
    }
}
