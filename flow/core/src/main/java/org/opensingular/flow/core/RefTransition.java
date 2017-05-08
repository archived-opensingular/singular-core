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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa uma potencial transição específica em um instância de tarefa.
 *
 * @author Daniel C. Bordin on 22/03/2017
 */
public final class RefTransition implements Serializable {

    private final TaskInstance originTaskInstance;

    private final String transitionName;

    private transient STransition transition;

    RefTransition(@Nonnull TaskInstance originTaskInstance) {
        this(originTaskInstance, findTransition(originTaskInstance, null));
    }

    RefTransition(@Nonnull TaskInstance originTaskInstance, @Nonnull String transitionName) {
        this(originTaskInstance, findTransition(originTaskInstance, transitionName));
    }

    RefTransition(@Nonnull TaskInstance originTaskInstance, @Nonnull STransition transition) {
        this.originTaskInstance = Objects.requireNonNull(originTaskInstance);
        this.transitionName = transition.getName();
        this.transition = transition;
    }

    /** Retorna a instância de processo a que pertence essa potencia transição. */
    @Nonnull
    public ProcessInstance getProcessInstance() {
        return originTaskInstance.getProcessInstance();
    }

    /** Retorna a instância de tarefa que será o ponto de partida da execução da transição. */
    @Nonnull
    public TaskInstance getOriginTaskInstance() {
        return originTaskInstance;
    }

    /** Retorna a definição de transição para qual aponta essa referência. */
    @Nonnull
    public STransition getTransition() {
        if (transition == null) {
            transition= findTransition(originTaskInstance, transitionName);
        }
        return transition;
    }

    @Nonnull
    private static STransition findTransition(@Nonnull TaskInstance taskInstance, @Nullable String transitionName) {
        if (transitionName == null) {
            return taskInstance.getFlowTaskOrException().resolveDefaultTransitionOrException();
        }
        return taskInstance.getFlowTaskOrException().getTransitionOrException(transitionName);
    }
}
