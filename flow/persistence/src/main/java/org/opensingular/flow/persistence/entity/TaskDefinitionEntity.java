/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
public class TaskDefinitionEntity extends AbstractTaskDefinitionEntity<ProcessDefinitionEntity, TaskVersionEntity, RoleTaskEntity> {
    private static final long serialVersionUID = 1L;

//    // bi-directional many-to-one association to TaskRight
//    @OneToMany(mappedBy = "taskDefinition")
//    private List<TaskRight> permissoesTarefas;
//
//    public List<TaskRight> getPermissoesTarefas() {
//        return permissoesTarefas;
//    }\
//
//    public void setPermissoesTarefas(List<TaskRight> permissoesTarefas) {
//        this.permissoesTarefas = permissoesTarefas;
//    }

}
