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


import javax.annotation.Nullable;

public class STaskEnd extends STask<STaskEnd> {

    private EventType eventType;

    public STaskEnd(FlowMap map, String name, String abbreviation) {
        super(map, name, abbreviation);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.END;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the type of BPMN event will be used to
     * render this end task (the symbol that goes inside the end symbol).
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public EventType getDisplayEventType() {
        return eventType;
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the type of BPMN event will be used to
     * render this end task (the symbol that goes inside the end symbol).
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    public void setDisplayEventType(@Nullable EventType eventType) {
        this.eventType = eventType;
    }
}
