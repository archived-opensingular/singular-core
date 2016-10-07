/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_PROCESSO database table.
 */
@Entity
@GenericGenerator(name = AbstractProcessVersionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VERSAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessVersionEntity extends AbstractProcessVersionEntity<ProcessDefinitionEntity, TaskVersionEntity> {
    private static final long serialVersionUID = 1L;

}
