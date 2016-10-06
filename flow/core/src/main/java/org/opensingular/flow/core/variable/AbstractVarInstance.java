/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.variable;

import org.opensingular.flow.core.property.MetaData;

public abstract class AbstractVarInstance implements VarInstance {

    private MetaData metaData;

    private final VarDefinition definition;

    private VarInstanceMap<?> changeListener;

    public AbstractVarInstance(VarDefinition definition) {
        this.definition = definition;
    }


    @Override
    public VarDefinition getDefinicao() {
        return definition;
    }

    @Override
    public String getStringDisplay() {
        return getDefinicao().toDisplayString(this);
    }

    @Override
    public String getStringPersistencia() {
        return getDefinicao().toPersistenceString(this);
    }

    @Override
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    @Override
    public void setChangeListner(VarInstanceMap<?> changeListener) {
        this.changeListener = changeListener;
    }

    protected final boolean needToNotifyAboutValueChanged() {
        return changeListener != null;
    }

    protected void notifyValueChanged() {
        changeListener.onValueChanged(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [definicao=" + getRef() + ", codigo=" + getValor() + "]";
    }
}
