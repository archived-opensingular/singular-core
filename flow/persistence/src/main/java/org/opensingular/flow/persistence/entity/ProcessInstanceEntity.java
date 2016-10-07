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
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_INSTANCIA_PROCESSO database table.
 */
@Entity
@GenericGenerator(name = AbstractProcessInstanceEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_INSTANCIA_PROCESSO", schema = Constants.SCHEMA)
public class ProcessInstanceEntity extends AbstractProcessInstanceEntity<Actor, ProcessVersionEntity, TaskInstanceEntity, VariableInstanceEntity, RoleInstanceEntity, ExecutionVariableEntity> {
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "processInstance", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @Where(clause = "DT_FIM is null")
    private List<TaskInstanceEntity> currentTasks;

    public List<TaskInstanceEntity> getCurrentTasks() {
        return currentTasks;
    }

    public void setCurrentTasks(List<TaskInstanceEntity> currentTasks) {
        this.currentTasks = currentTasks;
    }

    public TaskInstanceEntity getCurrentTask() {
        // O current task também pode ser uma task com o tipo End,
        // mas não tem como fazer isso com o @Where
//        if (currentTasks != null && currentTasks.size() == 1) {
//            return currentTasks.stream().findFirst().get();
//        } else if (currentTasks != null && currentTasks.size() != 1) {
//            throw new SingularFlowException("Esse fluxo possui mais de um estado atual, não é possível determinar um único estado atual");
//        }
        if (getTasks() != null && getTasks().size() > 0) {
            return getTasks().get(getTasks().size() - 1);
        }
        return null;
    }
}
