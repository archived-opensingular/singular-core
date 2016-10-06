/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.model;

import org.opensingular.form.AtrRef;
import org.opensingular.lib.wicket.util.model.IBooleanModel;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.model.NullOrEmptyModel;
import org.apache.wicket.model.IModel;

public class AttributeModel<T> implements IReadOnlyModel<T> {

    private final IModel<?> model;
    private final String    nomeCompletoAtributo;
    private final Class<T>  classeValorAtributo;

    public AttributeModel(IModel<?> model, AtrRef<?, ?, T> atrRef) {
        this.model = model;
        this.nomeCompletoAtributo = atrRef.getNameFull();
        this.classeValorAtributo = atrRef.getValueClass();
    }

    @Override
    public T getObject() {
        if (model instanceof ISInstanceAwareModel<?>)
            return ((ISInstanceAwareModel<?>) model).getMInstancia().getAttributeValue(nomeCompletoAtributo, classeValorAtributo);

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
