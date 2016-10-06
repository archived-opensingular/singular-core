/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.service;

import java.util.Set;

import org.opensingular.flow.core.authorization.AccessLevel;

public interface IFlowAuthorizationService {
    public Set<String> listProcessDefinitionsWithAccess(String userCod, AccessLevel accessLevel);
    public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel);
    public boolean hasAccessToProcessInstance(String processInstanceFullId, String userCod, AccessLevel accessLevel);
}
