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

package org.opensingular.flow.core.service;

import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleTask;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;

public interface IFlowDefinitionEntityService<CATEGORY extends IEntityCategory, FLOW_DEFINITION extends IEntityFlowDefinition,
        FLOW_VERSION extends IEntityFlowVersion, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion,
 TRANSITION extends IEntityTaskTransitionVersion, ROLE_DEFINITION extends IEntityRoleDefinition, ROLE_TASK extends IEntityRoleTask> {

    /**
     * Generates a new {@link IEntityFlowVersion} if {@link FlowDefinition} is
     * new or has changed
     */
    FLOW_VERSION generateEntityFor(FlowDefinition<?> flowDefinition);

    boolean isDifferentVersion(IEntityFlowVersion oldEntity, IEntityFlowVersion newEntity);
}
