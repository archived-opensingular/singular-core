/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_CATEGORIA database table.
 */
@Entity
@GenericGenerator(name = AbstractCategoryEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_CATEGORIA", schema = Constants.SCHEMA)
public class CategoryEntity extends AbstractCategoryEntity<ProcessDefinitionEntity> {

    private static final long serialVersionUID = 1L;

}
