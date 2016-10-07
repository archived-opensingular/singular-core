/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
