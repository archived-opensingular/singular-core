/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.util.wicket.model.IBooleanModel;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.model.NullOrEmptyModel;

public class AtributoModel<T> implements IReadOnlyModel<T> {

    private final IModel<?> model;
    private final String    nomeCompletoAtributo;
    private final Class<T>  classeValorAtributo;

    public AtributoModel(IModel<?> model, AtrRef<?, ?, T> atrRef) {
        this.model = model;
        this.nomeCompletoAtributo = atrRef.getNameFull();
        this.classeValorAtributo = atrRef.getValueClass();
    }

    @Override
    public T getObject() {
        if (model instanceof IMInstanciaAwareModel<?>)
            return ((IMInstanciaAwareModel<?>) model).getMInstancia().getAttributeValue(nomeCompletoAtributo, classeValorAtributo);

        return null;
    }

    @Override
    public void detach() {
        model.detach();
    }

    public IBooleanModel emptyModel() {
        return new NullOrEmptyModel(this);
    }
}
