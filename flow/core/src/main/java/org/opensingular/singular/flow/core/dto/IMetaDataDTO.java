/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.dto;

import java.io.Serializable;
import java.util.List;

public interface IMetaDataDTO extends Serializable {
    Integer getId();

    void setId(Integer id);

    String getTask();

    void setTask(String task);

    String getType();

    void setType(String type);

    String getExecutor();

    void setExecutor(String executor);

    List<ITransactionDTO> getTransactions();

    void setTransactions(List<ITransactionDTO> transactions);
}
