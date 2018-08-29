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

import javax.annotation.Nullable;

import org.opensingular.flow.core.DisplayType;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.STaskJava;
import org.opensingular.flow.core.TaskJavaBatchCall;
import org.opensingular.flow.core.TaskJavaCall;
import org.opensingular.schedule.IScheduleData;

public interface BuilderJava<SELF extends BuilderJava<SELF>> extends BuilderTaskSelf<SELF, STaskJava> {

    default  <T extends FlowInstance>  SELF call(TaskJavaCall<T> impl) {
        getTask().call(impl);
        return self();
    }

    default <T extends FlowInstance> SELF batchCall(TaskJavaBatchCall<T> implBloco, IScheduleData scheduleData) {
        getTask().batchCall(implBloco, scheduleData);
        return self();
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the type of BPMN node that will be used to
     * render this task.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    default SELF setDisplayType(@Nullable DisplayType displayType) {
        getTask().setDisplayType(displayType);
        return self();
    }
}