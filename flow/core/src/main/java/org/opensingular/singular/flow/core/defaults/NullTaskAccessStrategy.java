/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.defaults;

import org.opensingular.singular.flow.core.MTask;
import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.ProcessDefinition;
import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.flow.core.TaskAccessStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NullTaskAccessStrategy extends TaskAccessStrategy<ProcessInstance> {

    @Override
    public boolean canExecute(ProcessInstance instance, MUser user) {
        return true;
    }

    @Override
    public Set<Integer> getFirstLevelUsersCodWithAccess(ProcessInstance instancia) {
        return Collections.emptySet();
    }

    @Override
    public List<? extends MUser> listAllocableUsers(ProcessInstance instancia) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        return Collections.emptyList();
    }
}
