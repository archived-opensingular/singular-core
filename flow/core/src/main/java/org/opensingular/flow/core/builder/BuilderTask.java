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

import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.ITaskPredicate;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.StartedTaskListener;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.flow.core.property.MetaDataKey;

import javax.annotation.Nonnull;
import java.io.Serializable;

public interface BuilderTask {

    public STask<?> getTask();

    /**
     * Cria uma nova transição da task atual para a task destino informada
     */
    public default BuilderTransition<?> go(ITaskDefinition taskRefDestiny) {
        return go(taskRefDestiny.getName(), taskRefDestiny);
    }

    public BuilderTransition<?> go(String actionName, ITaskDefinition taskRefDestiny);

    /**
     * Adds an automatic transition to the given {@param taskRefDestiny} using the {@param condition} predicate to
     * decide when the transition should be made
     *
     * @param actionName
     * @param taskRefDestiny
     * @param condition
     * @return
     */
    public BuilderTransitionPredicate<?> go(String actionName, ITaskDefinition taskRefDestiny, ITaskPredicate condition);

    /**
     * Same as  {@link #go(String, ITaskDefinition, ITaskPredicate)}, but it set a default actionName
     * @param taskRefDestiny
     * @param condition
     * @return
     */
    default BuilderTransitionPredicate<?> go(ITaskDefinition taskRefDestiny, ITaskPredicate condition) {
        return go(taskRefDestiny.getName(), taskRefDestiny, condition);
    }

    public BuilderTask uiAccess(TaskAccessStrategy<?> accessStrategy);

    public BuilderTask addStartedTaskListener(StartedTaskListener startedTaskListener);

    @Nonnull
    public <T extends Serializable> BuilderTask setMetaDataValue(@Nonnull MetaDataKey<T> key, T value);

}