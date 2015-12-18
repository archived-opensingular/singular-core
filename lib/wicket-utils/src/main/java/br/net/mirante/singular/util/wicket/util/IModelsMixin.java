package br.net.mirante.singular.util.wicket.util;

import static java.util.Comparator.*;

import java.io.Serializable;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import br.net.mirante.singular.lambda.IConsumer;
import br.net.mirante.singular.lambda.IFunction;
import br.net.mirante.singular.lambda.ISupplier;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.model.ValueModel;

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
                return function.apply(rootModel.getObject());
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
