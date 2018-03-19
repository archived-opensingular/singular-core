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

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;
import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@SequenceGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, sequenceName = "SQ_CO_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
@Table(name = "TB_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
@Check(constraints ="TP_ESTRATEGIA_SEGURANCA IS NULL OR (TP_ESTRATEGIA_SEGURANCA IN ('D','E'))")
public class TaskDefinitionEntity extends AbstractTaskDefinitionEntity<FlowDefinitionEntity, TaskVersionEntity, RoleTaskEntity> {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "taskDefinition", fetch = FetchType.LAZY)
    private List<TaskPermissionEntity> taskPermissions;

    public List<TaskPermissionEntity> getTaskPermissions() {
        return taskPermissions;
    }

    public void setTaskPermissions(List<TaskPermissionEntity> taskPermissions) {
        this.taskPermissions = taskPermissions;
    }

}