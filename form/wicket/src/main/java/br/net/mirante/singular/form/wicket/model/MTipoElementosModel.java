/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public class MTipoElementosModel
    implements IReadOnlyModel<SType<SInstance>> {

    private Object rootTarget;

    public MTipoElementosModel(Object rootTarget) {
        this.rootTarget = rootTarget;
    }

    @Override
    public SType<SInstance> getObject() {
        return getTipoElementos(rootTarget);
    }

    @SuppressWarnings("unchecked")
    public static SType<SInstance> getTipoElementos(Object obj) {
        if (obj instanceof SIList<?>)
            return ((SIList<SInstance>) obj).getElementsType();
        if (obj instanceof STypeList<?, ?>)
            return ((STypeList<SType<SInstance>, SInstance>) obj).getElementsType();
        if (obj instanceof IModel<?>)
            return getTipoElementos(((IModel<?>) obj).getObject());

        throw new IllegalArgumentException();
    }

    @Override
    public void detach() {
        if (rootTarget instanceof IDetachable) {
            ((IDetachable) rootTarget).detach();
        }
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
        MTipoElementosModel other = (MTipoElementosModel) obj;
        if (rootTarget == null) {
            if (other.rootTarget != null)
                return false;
        } else if (!rootTarget.equals(other.rootTarget))
            return false;
        return true;
    }
}
