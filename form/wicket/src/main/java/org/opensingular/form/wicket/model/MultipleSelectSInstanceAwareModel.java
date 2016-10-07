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

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MultipleSelectSInstanceAwareModel extends AbstractSInstanceAwareModel<List<Serializable>> {

    private static final long serialVersionUID = -4455601838581324870L;

    private final IModel<? extends SInstance>     model;
    private final List<SelectSInstanceAwareModel> selects;

    public MultipleSelectSInstanceAwareModel(IModel<? extends SInstance> model) {
        this.model = model;
        this.selects = new ArrayList<>();
        if (model.getObject() instanceof SIList) {
            final SIList<?> list = (SIList<?>) model.getObject();
            for (int i = 0; i < list.size(); i += 1) {
                selects.add(new SelectSInstanceAwareModel(new SInstanceListItemModel<>(model, i), getCustomSelectConverterResolver()));
            }
        } else {
            throw new SingularFormException("Este model somente deve ser utilizado para tipo lista");
        }
    }

    @Override
    public SInstance getMInstancia() {
        return model.getObject();
    }

    @Override
    public List<Serializable> getObject() {
        return selects.stream().map(IModel::getObject).collect(Collectors.toList());
    }

    @Override
    public void setObject(List<Serializable> objects) {
        if (model.getObject() instanceof SIList<?>) {
            final SIList<?> list = (SIList<?>) model.getObject();
            list.clearInstance();
            selects.clear();
            for (int i = 0; i <= objects.size(); i += 1) {
                final Serializable o = objects.get(i);
                final SInstance newElement = list.addNew();
                model.getObject().asAtrProvider().getConverter().fillInstance(newElement, o);
                selects.add(new SelectSInstanceAwareModel(new SInstanceListItemModel<>(model, i), getCustomSelectConverterResolver()));
            }
        } else {
            throw new SingularFormException("Este model somente deve ser utilizado para tipo lista");
        }
    }

    public SelectSInstanceAwareModel.SelectConverterResolver getCustomSelectConverterResolver(){
        return si -> Optional.ofNullable(si.getParent().asAtrProvider().getConverter());
    }

}