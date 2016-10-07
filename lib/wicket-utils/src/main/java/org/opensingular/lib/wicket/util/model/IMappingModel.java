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

package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.IPredicate;

public interface IMappingModel<T> extends IReadOnlyModel<T> {

    @SuppressWarnings("unchecked")
    public static <T, M extends IMappingModel<T> & IWrapModel<T>> M of(IModel<T> model) {
        class WrapperMappingModel implements IMappingModel<T>, IWrapModel<T> {
            @Override
            public void detach() {
                model.detach();
            }
            @Override
            public T getObject() {
                return model.getObject();
            }
            @Override
            public void setObject(T object) {
                model.setObject(object);
            }
            @Override
            public IModel<T> getWrappedModel() {
                return model;
            }
        }
        return (M) new WrapperMappingModel();

    }

    default public void setObject(T object) {
        throw new UnsupportedOperationException("Model " + getClass() + " does not support setObject(Object)");
    }

    default public <U> IMappingModel<U> map(IFunction<T, U> getter) {
        final IModel<T> self = this;
        return new IMappingModel<U>() {
            @Override
            public void detach() {
                self.detach();
            }
            @Override
            public U getObject() {
                T object = self.getObject();
                return (object == null) ? null : getter.apply(object);
            }
        };
    }

    default public IMappingModel<T> filter(IPredicate<T> filter) {
        return map(it -> filter.test(it) ? it : null);
    }

    default <U> IMappingModel<U> map(IFunction<T, U> getter, IBiConsumer<T, U> setter) {
        final IMappingModel<T> self = this;
        return new IMappingModel<U>() {
            @Override
            public void detach() {
                self.detach();
            }
            @Override
            public U getObject() {
                T selfObject = self.getObject();
                return (selfObject == null) ? null : getter.apply(selfObject);
            }
            @Override
            public void setObject(U object) {
                T selfObject = self.getObject();
                if (selfObject != null)
                    setter.accept(selfObject, object);
            }
        };
    }
}
