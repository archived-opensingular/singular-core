/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.calculation.SimpleValueCalculation;

public interface SAttributeEnabled {

    default <V> void setAttributeCalculation(AtrRef<?, ?, V> atr, SimpleValueCalculation<V> value) {
        getDictionary().loadPackage(atr.getPackageClass());
        setAttributeCalculation(atr.getNameFull(), null, value);
    }

    <V> void setAttributeCalculation(String attributeFullName, String subPath, SimpleValueCalculation<V> value);

    default <V> void setAttributeValue(AtrRef<?, ?, V> atr, V value) {
        getDictionary().loadPackage(atr.getPackageClass());
        setAttributeValue(atr.getNameFull(), null, value);
    }

    default <V> void setAttributeValue(SAttribute defAttribute, Object value) {
        setAttributeValue(defAttribute.getName(), null, value);
    }

    default void setAttributeValue(String attributeName, Object value) {
        setAttributeValue(attributeName, null, value);
    }

    void setAttributeValue(String attributeFullName, String subPath, Object value);

    <V> V getAttributeValue(String attributeFullName, Class<V> resultClass);

    default <T> T getAttributeValue(AtrRef<?, ?, ?> atr, Class<T> resultClass) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), resultClass);
    }

    default <V> V getAttributeValue(AtrRef<?, ?, V> atr) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), atr.getValueClass());
    }

    default Object getAttributeValue(String attributeFullName) {
        return getAttributeValue(attributeFullName, null);
    }

    SDictionary getDictionary();

}
