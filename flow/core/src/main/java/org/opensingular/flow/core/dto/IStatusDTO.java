/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.dto;

import java.io.Serializable;

public interface IStatusDTO extends Serializable {
    String getProcessCode();

    void setProcessCode(String processCode);

    Integer getAmount();

    void setAmount(Integer amount);

    Integer getAverageTimeInDays();

    void setAverageTimeInDays(Integer averageTimeInDays);

    Integer getOpenedInstancesLast30Days();

    void setOpenedInstancesLast30Days(Integer openedInstancesLast30Days);

    Integer getFinishedInstancesLast30Days();

    void setFinishedInstancesLast30Days(Integer finishedInstancesLast30Days);
}
