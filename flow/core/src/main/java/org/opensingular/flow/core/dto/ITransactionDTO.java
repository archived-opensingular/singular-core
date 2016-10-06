/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.dto;

import java.io.Serializable;
import java.util.List;

public interface ITransactionDTO extends Serializable {
    String getName();

    void setName(String name);

    String getSource();

    void setSource(String source);

    String getTarget();

    void setTarget(String target);

    List<IParameterDTO> getParameters();

    void setParameters(List<IParameterDTO> parameters);
}
