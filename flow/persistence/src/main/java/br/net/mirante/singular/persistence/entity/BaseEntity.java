/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import java.io.Serializable;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;

@SuppressWarnings("serial")
abstract class BaseEntity<PK extends Serializable> extends br.net.mirante.singular.support.persistence.entity.BaseEntity<PK> implements IEntityByCod<PK> {

}
