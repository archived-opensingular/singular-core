/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.dto.GroupDTO;

public interface IFlowAuthorizationProvider {
    public IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO);
}
