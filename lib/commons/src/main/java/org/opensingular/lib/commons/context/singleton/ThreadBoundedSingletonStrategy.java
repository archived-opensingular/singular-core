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
