/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

public abstract class AbstractSInstanceCampoModel<I extends SInstance>
    extends AbstractSInstanceModel<I>
    implements IChainingModel<I> {

    private Object rootTarget;

    public AbstractSInstanceCampoModel(Object rootTarget) {
        this.rootTarget = rootTarget;
    }

    public String getPropertyExpression() {
        return propertyExpression();
    }

    protected abstract String propertyExpression();

    @Override
    @SuppressWarnings("unchecked")
    public I getObject() {
        if(getRootTarget() != null) {
            return (I) getRootTarget().getField(getPropertyExpression());
        } else {
            return null;
        }
    }

    public SIComposite getRootTarget() {
        return (SIComposite) ((rootTarget instanceof IModel<?>)
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rootTarget == null) ? 0 : rootTarget.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractSInstanceCampoModel<?> other = (AbstractSInstanceCampoModel<?>) obj;
        if (rootTarget == null) {
            if (other.rootTarget != null)
                return false;
        } else if (!rootTarget.equals(other.rootTarget))
            return false;
        return true;
    }
}
