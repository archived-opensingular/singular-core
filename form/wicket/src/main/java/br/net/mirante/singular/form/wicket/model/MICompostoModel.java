/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.IComponentInheritedModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;

public class MICompostoModel<T extends SIComposite>
    extends AbstractSInstanceModel<T>
    implements IComponentInheritedModel<T> {

    private Object target;

    public MICompostoModel(Object target) {
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) ((target instanceof IModel<?>)
            ? ((IModel<?>) target).getObject()
            : target);
    }
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void detach() {
        if (target instanceof IDetachable) {
            ((IDetachable) target).detach();
        }
    }

    public String propertyExpression(Component owner) {
        return owner.getId();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <W> IWrapModel<W> wrapOnInheritance(Component component) {
        return new AttachedCompoundPropertyModel(component);
    }
    public <S extends SInstance> IModel<S> bind(String property) {
        return new SInstanceCampoModel<S>(this, property);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        MICompostoModel<?> other = (MICompostoModel<?>) obj;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }

    private class AttachedCompoundPropertyModel<C extends SInstance> extends AbstractSInstanceCampoModel<C>
        implements IWrapModel<C> {
        private final Component owner;
        public AttachedCompoundPropertyModel(Component owner) {
            super(MICompostoModel.this);
            this.owner = owner;
        }
        @Override
        protected String propertyExpression() {
            return MICompostoModel.this.propertyExpression(owner);
        }
        @Override
        public IModel<T> getWrappedModel() {
            return MICompostoModel.this;
        }

        @Override
        public void detach() {
            super.detach();
            MICompostoModel.this.detach();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((owner == null) ? 0 : owner.hashCode());
            return result;
        }
        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            AttachedCompoundPropertyModel<?> other = (AttachedCompoundPropertyModel<?>) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (owner == null) {
                if (other.owner != null)
                    return false;
            } else if (!owner.equals(other.owner))
                return false;
            return true;
        }
        private MICompostoModel<?> getOuterType() {
            return MICompostoModel.this;
        }
    }
}
