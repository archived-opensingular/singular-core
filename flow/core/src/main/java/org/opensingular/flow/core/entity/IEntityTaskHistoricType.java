/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;

public interface IEntityTaskHistoricType extends IEntityByCod<Integer> {

    String getDescription();

    void setDescription(String description);
}
