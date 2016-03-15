/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.entity;


public interface IEntityTaskTransitionVersion extends IEntityByCod<Integer> {

    IEntityTaskVersion getOriginTask();

    IEntityTaskVersion getDestinationTask();

    String getName();

    void setName(String name);

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    TransitionType getType();

    void setType(TransitionType type);

}
