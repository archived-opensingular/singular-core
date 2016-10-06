/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

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
