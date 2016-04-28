/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface IEntityTaskDefinition extends IEntityByCod<Integer> {

    IEntityProcessDefinition getProcessDefinition();

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    List<? extends IEntityTaskVersion> getVersions();

    AccessStrategyType getAccessStrategyType();

    void setAccessStrategyType(AccessStrategyType accessStrategyType);

    default String getName(){
        return getLastVersion().getName();
    }

    default IEntityTaskVersion getLastVersion(){
        return getVersions().stream().collect(Collectors.maxBy(Comparator.comparing(IEntityTaskVersion::getVersionDate))).get();
    }
}
