/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.defaults;

import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.flow.core.UserRoleSettingStrategy;

import java.util.Collections;
import java.util.List;

public class EmptyUserRoleSettingStrategy extends UserRoleSettingStrategy<ProcessInstance> {

    @Override
    public List<? extends MUser> listAllocableUsers(ProcessInstance processInstance) {
            return Collections.emptyList();
        }

}