/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public abstract class UserRoleSettingStrategy<K extends ProcessInstance> implements Serializable {

    public abstract List<? extends MUser> listAllocableUsers(K instancia);

    public MUser getAutomaticAllocatedUser(K instancia, TaskInstance tarefa) {
        return null;
    }
}
