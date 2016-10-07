/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;

public interface IEntityRoleTask extends IEntityByCod<Integer> {

    IEntityRoleDefinition getRoleDefinition();

    IEntityTaskDefinition getTaskDefinition();

}
