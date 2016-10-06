/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database table.
 */
@Entity
@GenericGenerator(name = AbstractExecutionVariableEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VARIAVEL_EXECUCAO_TRANSICAO", schema = Constants.SCHEMA)
public class ExecutionVariableEntity extends AbstractExecutionVariableEntity<ProcessInstanceEntity, TaskInstanceEntity, VariableInstanceEntity, VariableTypeInstance> {
    private static final long serialVersionUID = 1L;

}
