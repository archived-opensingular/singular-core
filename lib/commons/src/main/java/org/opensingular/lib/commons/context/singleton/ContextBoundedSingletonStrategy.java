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
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ContextBoundedSingletonStrategy extends DelegationSingletonStrategy implements Loggable {

    /**
     * Used only when this SpringBoundedSingletonStrategy is registered as a SpringBean
     */
    private final ThreadBoundedSingletonStrategy tempSingleton = new ThreadBoundedSingletonStrategy();
    private InstanceBoundedSingletonStrategy springSingleton;

    public ContextBoundedSingletonStrategy() {
        tempSingleton.put(ContextBoundedSingletonStrategy.class, this);
    }

    /**
     * Automatically replaces the current {@link SingularSingletonStrategy} keeping all singletons already registered
     */
    @PostConstruct
    public void init() {
        //Configure setSpringSingleton
        springSingleton = new InstanceBoundedSingletonStrategy();
        SingularSingletonStrategy strategy = (SingularSingletonStrategy) SingularContext.get();
        //Migrate data from the previous singleton strategy
        this.putEntries(strategy);
        this.putEntries(this.tempSingleton);
        SingularContextSetup.reset();
        SingularContextSetup.setup(this);
    }

    /**
     * Spring destroy method
     */
    @PreDestroy
    public void destroy(){
        //cleaning up static reference to this bean since this context was shut down
        SingularContextSetup.reset();
    }

    @Nonnull
    @Override
    protected SingularSingletonStrategy getStrategyImpl() {
        return springSingleton == null ? tempSingleton : springSingleton;
    }
}

