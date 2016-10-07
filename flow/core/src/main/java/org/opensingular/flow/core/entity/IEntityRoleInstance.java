/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;

import java.util.Date;

import org.opensingular.flow.core.MUser;

public interface IEntityRoleInstance extends IEntityByCod<Integer> {

    IEntityRoleDefinition getRole();

    MUser getUser();

    Date getCreateDate();

    MUser getAllocatorUser();

    IEntityProcessInstance getProcessInstance();

}
