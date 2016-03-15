/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.service;

import java.util.Set;

import br.net.mirante.singular.flow.core.authorization.AccessLevel;

public interface IFlowAuthorizationService {
    public Set<String> listProcessDefinitionsWithAccess(String userCod, AccessLevel accessLevel);
    public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel);
    public boolean hasAccessToProcessInstance(String processInstanceFullId, String userCod, AccessLevel accessLevel);
}
