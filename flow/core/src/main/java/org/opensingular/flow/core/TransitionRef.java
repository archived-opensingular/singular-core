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
import java.util.Objects;

public final class TransitionRef {

    private final TaskInstance originTaskInstance;

    private final MTransition transition;

    public TransitionRef(@Nonnull TaskInstance originTaskInstance, @Nonnull MTransition transition) {
        this.originTaskInstance = Objects.requireNonNull(originTaskInstance);
        this.transition = Objects.requireNonNull(transition);
    }

    @Nonnull
    public ProcessInstance getProcessInstance() {
        return originTaskInstance.getProcessInstance();
    }

    @Nonnull
    public TaskInstance getOriginTaskInstance() {
        return originTaskInstance;
    }

    @Nonnull
    public MTransition getTransition() {
        return transition;
    }
}
