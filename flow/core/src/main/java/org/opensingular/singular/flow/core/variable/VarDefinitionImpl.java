/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.variable;

import org.opensingular.singular.flow.core.property.MetaData;
import org.opensingular.singular.flow.core.property.MetaDataRef;

import com.google.common.base.MoreObjects;

public class VarDefinitionImpl implements VarDefinition {

    private final String ref;

    private final String nome;

    private final VarType tipo;

    private boolean obrigatorio;

    private MetaData metaData;

    public VarDefinitionImpl(VarDefinition toCopy) {
        this(toCopy.getRef(), toCopy.getName(), toCopy.getType(), toCopy.isRequired());
        copy(toCopy);
    }

    public VarDefinitionImpl(String ref, String nome, VarType tipo, boolean obrigatorio) {
        this.ref = ref;
        this.nome = nome;
        this.tipo = tipo;
        this.obrigatorio = obrigatorio;
    }

    @Override
    public <T> VarDefinition setMetaDataValue(MetaDataRef<T> propRef, T value) {
        getMetaData().set(propRef, value);
        return this;
    }

    @Override
    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue) {
        return metaData == null ? defaultValue : MoreObjects.firstNonNull(getMetaData().get(propRef), defaultValue);
    }

    @Override
    public <T> T getMetaDataValue(MetaDataRef<T> propRef) {
        return metaData == null ? null : getMetaData().get(propRef);
    }

    MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    @Override
    public VarDefinition copy() {
        return new VarDefinitionImpl(this);
    }

    protected void copy(VarDefinition toCopy) {
        obrigatorio = toCopy.isRequired();
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public String getName() {
        return nome;
    }

    @Override
    public VarType getType() {
        return tipo;
    }

    @Override
    public void setRequired(boolean value) {
        obrigatorio = value;
    }

    @Override
    public VarDefinition required() {
        obrigatorio = true;
        return this;
    }

    @Override
    public boolean isRequired() {
        return obrigatorio;
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof VarDefinition))
            return false;
        return ref.equalsIgnoreCase(((VarDefinition) obj).getRef());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ref_=" + ref + ", nome_=" + nome + ", tipo_=" + tipo + "]";
    }
}
