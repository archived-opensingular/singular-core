/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.entity;


public interface IEntityRoleDefinition extends IEntityByCod<Integer> {

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    String getName();

    void setName(String name);

    IEntityProcessDefinition getProcessDefinition();

    void setProcessDefinition(IEntityProcessDefinition processDefinition);
}
