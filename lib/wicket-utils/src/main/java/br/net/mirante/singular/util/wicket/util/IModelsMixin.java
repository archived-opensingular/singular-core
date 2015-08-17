package br.net.mirante.singular.util.wicket.util;

import br.net.mirante.singular.util.wicket.lambda.IFunction;
import br.net.mirante.singular.util.wicket.lambda.ISupplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;

@SuppressWarnings({ "serial" })
public interface IModelsMixin extends Serializable {

    default public <T extends Serializable> Model<T> of() {
        return Model.of();
    }

    default public <T extends Serializable> Model<T> of(T value) {
        return Model.of(value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    default public <T, L extends List<? extends T> & Serializable> IModel<L> ofList(L list) {
        return Model.ofList((List) list);
    }

    default public <T> CompoundPropertyModel<T> compoundFrom(T obj) {
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
        };
    }

    default public <ROOT, T, F extends IFunction<ROOT, T> & Serializable> IModel<T> get(IModel<ROOT> root, F function) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                ROOT rootObj = root.getObject();
                return (rootObj == null) ? null : function.apply(rootObj);
            }
        };
    }

    default public <ROOT extends Serializable, T, F extends IFunction<ROOT, T>> IModel<T> getFrom(ROOT root, F function) {
        return get(Model.of(root), function);
    }

    default public <ROOT extends Serializable, T, F extends IFunction<Optional<ROOT>, Optional<T>>> IModel<T> getFromOptional(ROOT root, F function) {
        return getOptional(Model.of(root), function);
    }

    default public <ROOT extends Serializable, T, F extends IFunction<Optional<ROOT>, Optional<T>>> IModel<T> getOptional(IModel<ROOT> root, F function) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                ROOT rootObj = root.getObject();
                return (rootObj == null) ? null : function.apply(Optional.ofNullable(rootObj)).orElse(null);
            }
        };
    }

    default <T> PropertyModel<T> property(Object modelObject, String expression) {
        return new PropertyModel<>(modelObject, expression);
    }

    default IModel<Boolean> startsWith(String prefix, IModel<String> model) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                String obj = model.getObject();
                return (obj != null) && obj.startsWith(prefix);
            }
        };
    }

    default IModel<Boolean> not(IModel<Boolean> model) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !model.getObject();
            }
        };
    }

//    default <C extends Comparable<C>> IModel<Boolean> gt(IModel<C> lower, IModel<C> higher) {
//        return new AbstractReadOnlyModel<Boolean>() {
//            @Override
//            public Boolean getObject() {
//                return nullsFirst(comparing(IModel<C>::getObject)).compare(lower, higher) > 0;
//            }
//        };
//    }

    default <T> IModel<T> readOnly(ISupplier<T> supplier) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                return supplier.get();
            }
        };
    }

    default <T extends Serializable> IModel<T> readOnly(ISupplier<T> supplier, T defaultValue) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                return Optional.ofNullable(supplier.get()).orElse(defaultValue);
            }
        };
    }

    @SuppressWarnings("unchecked")
    default <T extends Serializable> IModel<T> wrapValue(Serializable valueOrModel) {
        return (valueOrModel instanceof IModel) ? (IModel<T>) valueOrModel : this.of((T) valueOrModel);
    }

    default IModel<Boolean> empty(IModel<?> model) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return isEmpty(model);
            }

            private Boolean isEmpty(final Object obj) {
                if (obj == null) {
                    return true;

                } else if (obj instanceof IModel<?>) {
                    return isEmpty(((IModel<?>) obj).getObject());

                } else if (obj instanceof CharSequence) {
                    return StringUtils.isBlank(obj.toString());

                } else if (obj instanceof Collection<?>) {
                    return ((Collection<?>) obj).isEmpty();
                } else {
                    return obj instanceof Map<?, ?> && ((Map<?, ?>) obj).isEmpty();
                }
            }
        };
    }
}
