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
        strategy = null;
    }

    @Nonnull
    @Override
    protected SingularSingletonStrategy getStrategyImpl() {
        return SingularContextImpl.strategy;
    }
}
