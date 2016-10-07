/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
