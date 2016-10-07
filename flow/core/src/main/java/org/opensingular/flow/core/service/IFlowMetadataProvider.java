/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.service;

import org.opensingular.flow.core.dto.GroupDTO;

public interface IFlowMetadataProvider {

    IFlowMetadataService getMetadataService(GroupDTO groupDTO);
}
