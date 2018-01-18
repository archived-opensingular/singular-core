/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.provider;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProviderLoader {

    private boolean enableDanglingValues;
    ;
    private ISupplier<SInstance> instanceISupplier;

    public ProviderLoader(ISupplier<SInstance> instanceISupplier, boolean enableDanglingValues) {
        this.instanceISupplier = instanceISupplier;
        this.enableDanglingValues = enableDanglingValues;
    }

    public ProviderLoader(SInstance instance, boolean ajaxRequest) {
        this(() -> instance, ajaxRequest);
    }

    @SuppressWarnings("unchecked")
    public List<Serializable> load(ProviderContext<?> providerContext) {
        final SInstance          instance = instanceISupplier.get();
        final Provider           provider = instance.asAtrProvider().getProvider();
        final List<Serializable> values   = new ArrayList<>();

        if (provider != null) {
            final List<Serializable> result = provider.load(providerContext);
            if (result != null) {
                values.addAll(result);
            }

            //Dangling values
            if (!instance.isEmptyOfData()) {
                final SInstanceConverter        converter  = instance.asAtrProvider().getConverter();
                final List<Object>              ids        = new ArrayList<>();
                final IFunction<Object, Object> idFunction = instance.asAtrProvider().getIdFunction();
            /*Collect All Ids*/
                values.forEach(v -> ids.add(idFunction.apply(v)));
                List<SInstance> selectedInstances = new ArrayList<>();
                if (instance instanceof SIList) {
                    ((SIList<SInstance>) instance).forEach(selectedInstances::add);
                } else {
                    selectedInstances.add(instance);
                }

                collectSelectedInstances(instance, values, converter, ids, idFunction, selectedInstances);
            }
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public void collectSelectedInstances(SInstance instance, List<Serializable> values, SInstanceConverter converter,
                                         List<Object> ids, IFunction<Object, Object> idFunction,
                                         List<SInstance> selectedInstances) {

        for (int i = 0; i < selectedInstances.size(); i += 1) {
            SInstance          ins       = selectedInstances.get(i);
            final Serializable converted = converter.toObject(ins);
            if (converted != null && !ids.contains(idFunction.apply(converted))) {
                if (!enableDanglingValues) {
                    instance.clearInstance();
                } else {
                    values.add(i, converted);
                }
            }
        }
    }


    public List<Serializable> load() {
        return load(ProviderContext.of(instanceISupplier.get()));
    }
}
