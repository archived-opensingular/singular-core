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

import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.lib.commons.lambda.IFunction;

import java.io.Serializable;
import java.util.Optional;

public class SelectSInstanceAwareModel extends AbstractSInstanceAwareModel<Serializable> {

    private static final long serialVersionUID = -4455601838581324870L;

    private final IModel<? extends SInstance> model;

    private final SelectConverterResolver resolver;

    public SelectSInstanceAwareModel(IModel<? extends SInstance> model) {
        this.model = model;
        this.resolver = sInstance -> Optional.ofNullable(sInstance.asAtrProvider().getConverter());
    }

    public SelectSInstanceAwareModel(IModel<? extends SInstance> model, SelectConverterResolver resolver) {
        this.model = model;
        this.resolver = resolver;
    }

    @Override
    public SInstance getSInstance() {
        return model.getObject();
    }

    @Override
    public Serializable getObject() {
        SInstance instance = getSInstance();
        if (instance.isEmptyOfData()) {
            return null;
        }
        Optional<SInstanceConverter> converter = resolver.apply(instance);
        if (converter.isPresent()) {
            return converter.get().toObject(instance);
        } else if (instance instanceof SIComposite) {
            throw new SingularFormException("Nenhum converter foi informado para o tipo " + instance.getName(), instance);
        }
        return (Serializable) instance.getValue();
    }

    @Override
    public void setObject(Serializable object) {
        SInstance instance = getSInstance();
        if (object == null) {
            instance.clearInstance();
        } else {
            Optional<SInstanceConverter> converter = resolver.apply(instance);
            if (converter.isPresent()) {
                converter.get().fillInstance(instance, object);
            } else if (instance instanceof SIComposite) {
                throw new SingularFormException("Nenhum converter foi informado para o tipo " + instance.getName(),
                        instance);
            } else {
                instance.setValue(object);
            }
        }
    }

    /**
     * interface utilizada para determinar como o converter será encontrado a partir da miinstancia alvo
     * da atualização do modelo.
     * <p>
     * Esse resolver é configurado por padrão, mas pode ser sobrescrito caso seja necessário
     * encontrar o converter de uma maneira diferente.
     */
    @FunctionalInterface
    public static interface SelectConverterResolver extends IFunction<SInstance, Optional<SInstanceConverter>> {
        public Optional<SInstanceConverter> apply(SInstance instance);
    }
}
