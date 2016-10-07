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

import org.opensingular.flow.core.IExecutionDateStrategy;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.flow.core.ITaskPageStrategy;
import org.opensingular.flow.core.MTaskUserExecutable;
import org.opensingular.flow.core.ProcessInstance;

public interface BUserExecutable<SELF extends BUserExecutable<SELF, TASK>, TASK extends MTaskUserExecutable<?>> extends BuilderTaskSelf<SELF, TASK> {

    @Override
    public default SELF addAccessStrategy(TaskAccessStrategy<?> accessStrategy) {
        getTask().addAccessStrategy(accessStrategy);
        return self();
    }

    @Override
    public default SELF addVisualizeStrategy(TaskAccessStrategy<?> accessStrategy) {
        getTask().addVisualizeStrategy(accessStrategy);
        return self();
    }

    public default SELF withExecutionPage(ITaskPageStrategy executionPage) {
        getTask().setExecutionPage(executionPage);
        return self();
    }

    public default SELF afterTaskGoTo(ITaskPageStrategy pageAfterTask) {
        getTask().setPageAfterTask(pageAfterTask);
        return self();
    }

    public default SELF withBackPage(ITaskPageStrategy backPage) {
        getTask().setBackPage(backPage);
        return self();
    }

    public default <T extends ProcessInstance> SELF withTargetDate(IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        getTask().withTargetDate(targetDateExecutionStrategy);
        return self();
    }
}