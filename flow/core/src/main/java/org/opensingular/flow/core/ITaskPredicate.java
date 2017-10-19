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

import java.util.function.Predicate;

public interface ITaskPredicate extends Predicate<TaskInstance> {

    String getName();

    /**
     * Return the BPMN type of event that triggers the execution of this transition for use when generating a diagram
     * of the process.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     */
    EventType getDisplayEventType();

    default String getFullDescription() {
        return getName();
    }

    default String getDescription(TaskInstance taskInstance) {
        return getFullDescription();
    }
}
