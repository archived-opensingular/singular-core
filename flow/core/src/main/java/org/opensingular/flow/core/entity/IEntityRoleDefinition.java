/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;


public interface IEntityRoleDefinition extends IEntityByCod<Integer> {

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    String getName();

    void setName(String name);

    IEntityProcessDefinition getProcessDefinition();

    void setProcessDefinition(IEntityProcessDefinition processDefinition);
}
