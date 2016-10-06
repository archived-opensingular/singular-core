/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeSimple;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;

import java.util.List;

@SuppressWarnings("serial")
public class SInstanceValueModel<T>
    implements
    IModel<T>,
    IObjectClassAwareModel<T>,
        ISInstanceAwareModel<T> {

    private IModel<? extends SInstance> instanciaModel;

    public SInstanceValueModel(IModel<? extends SInstance> instanciaModel) {
        this.instanciaModel = instanciaModel;
    }

    public SInstance getTarget() {
        return instanciaModel.getObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) getTarget().getValue();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setObject(T object) {
        SInstance target = getTarget();
        if (target instanceof SIList) {
            target.clearInstance();
            ((List) object).forEach(((SIList) target)::addValue);
        } else {
            target.setValue(object);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getObjectClass() {
        SType<?> mtipo = getTarget().getType();
        if (mtipo instanceof STypeSimple<?, ?>) {
            return (Class<T>) ((STypeSimple<?, ?>) mtipo).getValueClass();
        }
        return (Class<T>) mtipo.getInstanceClass();
    }

    @Override
    public SInstance getMInstancia() {
        return instanciaModel.getObject();
    }

    @Override
    public void detach() {
        this.instanciaModel.detach();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanciaModel == null) ? 0 : instanciaModel.hashCode());
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
        SInstanceValueModel<?> other = (SInstanceValueModel<?>) obj;
        if (instanciaModel == null) {
            if (other.instanciaModel != null)
                return false;
        } else if (!instanciaModel.equals(other.instanciaModel))
            return false;
        return true;
    }
}