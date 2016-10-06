/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.opensingular.singular.support.persistence.util.Constants;


/**
 * The persistent class for the TB_GRUPO_PROCESSO database table.
 */
@Entity
@Table(name = "TB_GRUPO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessGroupEntity extends AbstractProcessGroupEntity {

    private static final long serialVersionUID = 1L;

}