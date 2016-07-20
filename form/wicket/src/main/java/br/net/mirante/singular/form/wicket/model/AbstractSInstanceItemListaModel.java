/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;

public abstract class AbstractSInstanceItemListaModel<I extends SInstance>
    extends AbstractSInstanceModel<I>
    implements IChainingModel<I> {

    private Object rootTarget;

    public AbstractSInstanceItemListaModel(Object rootTarget) {
        this.rootTarget = rootTarget;
    }

    public int getIndex() {
        return index();
    }

    protected abstract int index();

    @Override
    @SuppressWarnings("unchecked")
    public I getObject() {
        SIList<I> iLista = getRootTarget();
        if (iLista == null || getIndex() >= iLista.size())
            return null;
        return (I) iLista.get(getIndex());
    }

    @SuppressWarnings("unchecked")
    public SIList<I> getRootTarget() {
        return (SIList<I>) ((rootTarget instanceof IModel<?>)
            ? ((IModel<?>) rootTarget).getObject()
            : rootTarget);
    }

    @Override
    public void detach() {
        if (rootTarget instanceof IDetachable) {
            ((IDetachable) rootTarget).detach();
        }
    }

    @Override
    public void setChainedModel(IModel<?> rootModel) {
        this.rootTarget = rootModel;
    }
    @Override
    public IModel<?> getChainedModel() {
        return (rootTarget instanceof IModel) ? (IModel<?>) rootTarget : null;
    }

    @Override
    public int hashCode() {
        final I object = this.getObject();
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.getPathFull().hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final AbstractSInstanceItemListaModel<?> other = (AbstractSInstanceItemListaModel<?>) obj;
        final I object = this.getObject();
        final I otherObject = (I) other.getObject();

        if (object == null) {
            if (otherObject != null)
                return false;
        } else if (!object.getPathFull().equals(otherObject.getPathFull()))
            return false;
        if (getIndex() != other.getIndex())
            return false;
        return true;
    }
}
