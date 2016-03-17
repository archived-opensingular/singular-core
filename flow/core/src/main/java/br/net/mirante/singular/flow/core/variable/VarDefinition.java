/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.variable;

import br.net.mirante.singular.flow.core.property.MetaData;
import br.net.mirante.singular.flow.core.property.MetaDataRef;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public interface VarDefinition extends Serializable{

    public String getRef();

    public String getName();

    public VarType getType();

    public void setRequired(boolean value);

    public VarDefinition required();

    public boolean isRequired();

    public VarDefinition copy();

    public default String toDisplayString(VarInstance varInstance) {
        return getType().toDisplayString(varInstance);
    }

    public default String toDisplayString(Object valor) {
        return getType().toDisplayString(valor, this);
    }

    public default String toPersistenceString(VarInstance varInstance) {
        return getType().toPersistenceString(varInstance);
    }

    public <T> VarDefinition setMetaDataValue(MetaDataRef<T> propRef, T value);

    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue);

    public <T> T getMetaDataValue(MetaDataRef<T> propRef);

}
