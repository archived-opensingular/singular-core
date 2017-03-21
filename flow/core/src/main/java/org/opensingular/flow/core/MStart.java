/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import org.opensingular.flow.core.variable.VarInstanceMap;

import javax.annotation.Nonnull;

/**
 * Representa um ponto de inicialização de um fluxo do processo.
 *
 * @author Daniel C. Bordin on 19/03/2017.
 */
public class MStart extends MParametersEnabled {

    private final MTask<?> task;

    private IStartInitializer startInitializer;

    MStart(MTask<?> task) {
        this.task = task;
    }

    /** Task a ser executada a partir desse ponto de inicialização. */
    public MTask<?> getTask() {
        return task;
    }

    public <I extends ProcessInstance> IStartInitializer<I> getStartInitializer() {
        return startInitializer;
    }

    public <I extends ProcessInstance> void setStartInitializer(IStartInitializer<I> startInitializer) {
        this.startInitializer = startInitializer;
    }

    @Nonnull
    final VarInstanceMap<?> newCallParameters() {
        VarInstanceMap<?> params = getParameters().newInstanceMap();
        //        if (parametersInitializer != null) {
        //            parametersInitializer.init(transitionRef, params);
        //        }
        return params;
    }


    @Override
    FlowMap getFlowMap() {
        return task.getFlowMap();
    }

    @FunctionalInterface
    public static interface IStartInitializer<I extends ProcessInstance> {
        public void startInstance(I instance, StartCall<I> startCall);
    }
}
