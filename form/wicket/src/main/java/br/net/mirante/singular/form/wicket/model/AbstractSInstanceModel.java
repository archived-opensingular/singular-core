/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;

public abstract class AbstractSInstanceModel<I extends SInstance>
    implements IModel<I>, IMInstanciaAwareModel<I> {

    @Override
    public final SInstance getMInstancia() {
        return getObject();
    }

    @Override
    public void setObject(I object) {
        throw new UnsupportedOperationException("Model " + getClass() +
            " does not support setObject(Object)");
    }

    @Override
    public void detach() {}
}