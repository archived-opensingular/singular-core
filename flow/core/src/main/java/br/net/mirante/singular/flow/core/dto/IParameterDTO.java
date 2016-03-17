/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.dto;

import java.io.Serializable;

public interface IParameterDTO extends Serializable {
    String getName();

    void setName(String name);

    boolean isRequired();

    void setRequired(boolean required);
}
