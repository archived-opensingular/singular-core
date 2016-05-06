/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the RL_PAPEL_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractRoleTaskEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "RL_PAPEL_TAREFA", schema = Constants.SCHEMA)
public class RoleTaskEntity extends AbstractRoleTaskEntity<TaskDefinitionEntity, RoleDefinitionEntity> {
    private static final long serialVersionUID = 1L;

}
