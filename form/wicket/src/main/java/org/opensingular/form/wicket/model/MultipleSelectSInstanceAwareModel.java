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
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class MultipleSelectSInstanceAwareModel extends AbstractSInstanceAwareModel<List<Serializable>> {

    private static final long serialVersionUID = -4455601838581324870L;

    private final IModel<? extends SIList<?>> model;
    private final List<SelectSInstanceAwareModel> selects;

    public MultipleSelectSInstanceAwareModel(IModel<? extends SInstance> model) {
        if (!(model.getObject() instanceof SIList)) {
            throw new SingularFormException("Este model somente deve ser utilizado para tipo lista", model.getObject());
        }
        this.model = (IModel<? extends SIList<?>>) model;
        this.selects = new ArrayList<>();
        final SIList<?> list = this.model.getObject();
        for (int i = 0; i < list.size(); i += 1) {
            selects.add(new SelectSInstanceAwareModel(new SInstanceListItemModel<>(model, i), getCustomSelectConverterResolver()));
        }
    }

    @Override
    public SInstance getSInstance() {
        return model.getObject();
    }

    @Override
    public List<Serializable> getObject() {
        return selects.stream().map(IModel::getObject).collect(Collectors.toList());
    }

    @Override
    public void setObject(List<Serializable> objects) {
        SIList list = model.getObject();
        SInstanceConverter converter = model.getObject().asAtrProvider().getConverter();
        Map<Serializable, SInstance> deletedValuesMap = makeValueInstanceMap(list, converter);

        //remove submited fields from deletion and store new values
        List<Serializable> newValues = new ArrayList<>();
        for (Serializable next : objects) {
            if (deletedValuesMap.containsKey(next)) {
                deletedValuesMap.remove(next);
            } else {
                newValues.add(next);
            }
        }

        //delete remove values from silist
        deletedValuesMap.forEach((key, val) -> {
            list.remove(val);
        });

        //convert new values and add to the list
        for (Serializable newValue : newValues) {
            addNewValue(list, converter, newValue);
        }
    }

    private void addNewValue(SIList list, SInstanceConverter converter, Serializable newValue) {
        final SInstance newElement = list.addNew();
        converter.fillInstance(newElement, newValue);
        selects.add(new SelectSInstanceAwareModel(new SInstanceListItemModel<>(model, list.indexOf(newElement)), getCustomSelectConverterResolver()));
    }

    private Map<Serializable, SInstance> makeValueInstanceMap(SIList list, SInstanceConverter converter) {
        Map<Serializable, SInstance> valueInstanceMap = new LinkedHashMap<>();
        list.forEach(instance -> {
            valueInstanceMap.put(converter.toObject((SInstance) instance), (SInstance) instance);
        });
        return valueInstanceMap;
    }

    public SelectSInstanceAwareModel.SelectConverterResolver getCustomSelectConverterResolver() {
        return si -> Optional.ofNullable(si.getParent().asAtrProvider().getConverter());
    }

}
