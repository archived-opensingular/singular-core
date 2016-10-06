/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.portlet.filter;


import java.util.Date;

import org.opensingular.singular.bamclient.exeptions.BamClientExeption;

public enum FieldType {

    BOOLEAN(Boolean.class),
    INTEGER(Integer.class),
    TEXT(String.class),
    TEXTAREA,
    SELECTION,
    DATE(Date.class),
    AGGREGATION_PERIOD(AggregationPeriod.class),
    DEFAULT;

    private Class[] defaultTypeForClasses;

    FieldType(Class... defaultTypeForClasses) {
        this.defaultTypeForClasses = defaultTypeForClasses;
    }

    public static FieldType getDefaultTypeForClass(Class clazz) {
        for (FieldType fieldType : values()) {
            for (Class compatibleClass : fieldType.defaultTypeForClasses) {
                if (clazz.equals(compatibleClass)) {
                    return fieldType;
                }
            }
        }
        throw new BamClientExeption("Não existe um tipo default para a classe " + clazz);
    }
}
