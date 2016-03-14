/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityRoleInstance extends IEntityByCod<Integer> {

    IEntityRoleDefinition getRole();

    MUser getUser();

    Date getCreateDate();

    MUser getAllocatorUser();

    IEntityProcessInstance getProcessInstance();

}
