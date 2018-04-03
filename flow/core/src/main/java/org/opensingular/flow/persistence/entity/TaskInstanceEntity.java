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

package org.opensingular.flow.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 */
@Entity
@SequenceGenerator(name = AbstractTaskInstanceEntity.PK_GENERATOR_NAME, sequenceName = Constants.SCHEMA + ".SQ_CO_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
@Table(name = "TB_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstanceEntity extends AbstractTaskInstanceEntity<Actor, FlowInstanceEntity, TaskVersionEntity, TaskTransitionVersionEntity, ExecutionVariableEntity, TaskInstanceHistoryEntity> {
    private static final long serialVersionUID = 1L;

}
