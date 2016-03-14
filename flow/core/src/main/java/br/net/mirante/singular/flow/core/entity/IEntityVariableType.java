/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.entity;

public interface IEntityVariableType extends IEntityByCod<Integer> {

    String getTypeClassName();

    void setTypeClassName(String typeClassName);

    String getDescription();

    void setDescription(String description);

}
