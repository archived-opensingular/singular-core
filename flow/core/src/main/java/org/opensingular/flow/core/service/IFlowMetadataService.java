/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.service;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.opensingular.flow.core.authorization.AccessLevel;

public interface IFlowMetadataService {

    Set<String> listProcessDefinitionsWithAccess(@NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessDefinition(@NotNull String processDefinitionKey, @NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessInstance(@NotNull String processInstanceFullId, @NotNull String userCod, @NotNull AccessLevel accessLevel);

    byte[] processDefinitionDiagram(@NotNull String processDefinitionKey);
}
