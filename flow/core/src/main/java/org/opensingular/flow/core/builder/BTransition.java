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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.TransitionAccessStrategy;
import org.opensingular.flow.core.MTransition;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.TransitionAccessStrategyImpl;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface BTransition<SELF extends BTransition<SELF>> {

    public abstract MTransition getTransition();

    public FlowBuilder getFlowBuilder();

    public default SELF self() {
        return (SELF) this;
    }

    public default SELF markAsDefault() {
        getTransition().getOrigin().setDefaultTransition(getTransition());
        return (SELF) self();
    }

    public default SELF thenGo(ITaskDefinition destination) {
        MTransition transition = getTransition().thenGo(destination);
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF thenGo(String actionName, ITaskDefinition destination) {
        MTransition transition = getTransition().thenGo(actionName, destination);
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF hideInExecution() {
        getTransition().withAccessControl(TransitionAccessStrategyImpl.enabled(false));
        return self();
    }
    
    // depois de avaliar a assinatura com ITaskDefinition e refatorar Alocpro,
    // apagar esse método
    @Deprecated
    public default SELF thenGo(BTask destination) {
        MTransition transition = getTransition().thenGo(destination.getTask());
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    // depois de avaliar a assinatura com ITaskDefinition e refatorar Alocpro,
    // apagar esse método
    @Deprecated
    public default SELF thenGo(String actionName, BTask destination) {
        MTransition transition = getTransition().thenGo(actionName, destination.getTask());
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF withAccessControl(TransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        getTransition().withAccessControl(accessStrategy);
        return self();
    }

    public default SELF defineUserRoleInTransition(BProcessRole<?> processRole) {
        getTransition().defineUserRoleInTransition(processRole.getProcessRole());
        return self();
    }
}