/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.dto;

import java.io.Serializable;
import java.util.List;

public interface IMenuItemDTO extends Serializable {
    Integer getId();

    String getName();

    String getCode();

    Integer getCounter();

    List<IMenuItemDTO> getItens();

    IMenuItemDTO addItem(IMenuItemDTO item);
}
