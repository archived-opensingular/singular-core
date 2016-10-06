/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;

import java.util.Date;

public interface IEntityExecutionVariable extends IEntityByCod<Integer> {

    IEntityProcessInstance getProcessInstance();

    String getName();

    void setName(String name);

    IEntityTaskInstance getOriginTask();

    IEntityTaskInstance getDestinationTask();

    String getValue();

    void setValue(String value);

    Date getDate();

    void setDate(Date date);
}