package br.net.mirante.singular.util.wicket.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import br.net.mirante.singular.commons.lambda.IBiConsumer;
import br.net.mirante.singular.commons.lambda.IFunction;

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
