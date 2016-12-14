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

package org.opensingular.lib.wicket.util.util;

import static java.util.Comparator.*;

import java.io.Serializable;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.model.ValueModel;

@SuppressWarnings({ "serial" })
public interface IModelsMixin extends Serializable {

    default public <T extends Serializable> ValueModel<T> ofValue() {
        return ofValue(null);
    }

    default public <T extends Serializable> ValueModel<T> ofValue(T value) {
        return ofValue(value, it -> it);
    }

    default public <T extends Serializable> ValueModel<T> ofValue(T value, IFunction<T, Object> equalsHashArgsFunc) {
        return new ValueModel<T>(value, equalsHashArgsFunc);
    }

    default public <T> CompoundPropertyModel<T> compoundOf(T obj) {
        return new CompoundPropertyModel<>(obj);
    }

    default public <T> CompoundPropertyModel<T> compound(IModel<T> model) {
        return new CompoundPropertyModel<>(model);
    }

    default public <T> PropertyModel<T> property(Serializable obj, String expr) {
        return new PropertyModel<>(obj, expr);
    }

    default public <T> PropertyModel<T> property(Serializable obj, String expr, Class<T> type) {
        return new PropertyModel<>(obj, expr);
    }

    default public <T> IModel<T> conditional(IModel<Boolean> test, IModel<T> ifTrue, IModel<T> ifFalse) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                return (Boolean.TRUE.equals(test.getObject()))
                    ? ifTrue.getObject()
                    : ifFalse.getObject();
            }
            @Override
            public void detach() {
                test.detach();
                ifTrue.detach();
                ifFalse.detach();
            }
        };
    }

    default public <T, U> IModel<U> map(IModel<T> rootModel, IFunction<T, U> function) {
        return new IReadOnlyModel<U>() {
            @Override
            public U getObject() {
                T root = rootModel.getObject();
                return (root == null) ? null : function.apply(root);
            }
            @Override
            public void detach() {
                rootModel.detach();
            }
        };
    }

    default public <T> IModel<T> get(ISupplier<T> supplier) {
        return new IReadOnlyModel<T>() {
            @Override
            public T getObject() {
                return supplier.get();
            }
        };
    }

    default public <T> IModel<T> getSet(ISupplier<T> getter, IConsumer<T> setter) {
        return new IModel<T>() {
            @Override
            public T getObject() {
                return getter.get();
            }
            @Override
            public void setObject(T object) {
                setter.accept(object);
            }
            @Override
            public void detach() {}
        };
    }

    default public <T> LoadableDetachableModel<T> loadable(ISupplier<T> supplier) {
        return new LoadableDetachableModel<T>() {
            @Override
            protected T load() {
                return supplier.get();
            }
        };
    }

    default public <T> LoadableDetachableModel<T> loadable(T initialValue, ISupplier<T> supplier) {
        return new LoadableDetachableModel<T>(initialValue) {
            @Override
            protected T load() {
                return supplier.get();
            }
        };
    }

    default <T> PropertyModel<T> property(Object modelObject, String expression) {
        return new PropertyModel<>(modelObject, expression);
    }

    default IModel<Boolean> isNullOrEmpty(Serializable modelOrValue) {
        return (IReadOnlyModel<Boolean>) () -> WicketUtils.nullOrEmpty(modelOrValue);
    }

    default IModel<Boolean> isNotNullOrEmpty(Serializable modelOrValue) {
        return (IReadOnlyModel<Boolean>) () -> !WicketUtils.nullOrEmpty(modelOrValue);
    }

    default IReadOnlyModel<Boolean> isNot(IModel<Boolean> model) {
        return new IReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !model.getObject();
            }
            @Override
            public void detach() {
                model.detach();
            }
        };
    }

    default <C extends Comparable<C>> IReadOnlyModel<Boolean> isGt(IModel<C> lower, IModel<C> higher) {
        return new IReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return nullsFirst(comparing(IModel<C>::getObject)).compare(lower, higher) > 0;
            }
            @Override
            public void detach() {
                lower.detach();
                higher.detach();
            }
        };
    }

    @SuppressWarnings("unchecked")
    default <T extends Serializable> IModel<T> wrapValue(Serializable valueOrModel) {
        return (valueOrModel instanceof IModel) ? (IModel<T>) valueOrModel : this.ofValue((T) valueOrModel);
    }
}
