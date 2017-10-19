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
import org.apache.wicket.model.IObjectClassAwareModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;

import java.util.List;

@SuppressWarnings("serial")
public class SInstanceValueModel<T>
    implements
    IModel<T>,
    IObjectClassAwareModel<T>,
        ISInstanceAwareModel<T> {

    private IModel<? extends SInstance> instanceModel;

    public SInstanceValueModel(IModel<? extends SInstance> instanceModel) {
        this.instanceModel = instanceModel;
    }

    public SInstance getTarget() {
        return instanceModel.getObject();
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
        SType<?> type = getTarget().getType();
        if (type instanceof STypeSimple<?, ?>) {
            return (Class<T>) ((STypeSimple<?, ?>) type).getValueClass();
        }
        return (Class<T>) type.getInstanceClass();
    }

    @Override
    public SInstance getSInstance() {
        return instanceModel.getObject();
    }

    @Override
    public void detach() {
        this.instanceModel.detach();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanceModel == null) ? 0 : instanceModel.hashCode());
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
        if (instanceModel == null) {
            if (other.instanceModel != null)
                return false;
        } else if (!instanceModel.equals(other.instanceModel))
            return false;
        return true;
    }
}