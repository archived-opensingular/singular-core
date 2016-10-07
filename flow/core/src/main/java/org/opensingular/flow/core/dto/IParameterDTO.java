/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.dto;

import java.io.Serializable;

public interface IParameterDTO extends Serializable {
    String getName();

    void setName(String name);

    boolean isRequired();

    void setRequired(boolean required);
}
