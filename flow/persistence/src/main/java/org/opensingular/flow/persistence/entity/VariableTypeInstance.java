/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_TIPO_VARIAVEL database table.
 */
@Entity
@Table(name = "TB_TIPO_VARIAVEL", schema = Constants.SCHEMA)
@GenericGenerator(name = AbstractVariableTypeEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class VariableTypeInstance extends AbstractVariableTypeEntity {
    private static final long serialVersionUID = 1L;

}
