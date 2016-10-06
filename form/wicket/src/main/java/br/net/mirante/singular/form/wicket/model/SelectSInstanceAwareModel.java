package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.commons.lambda.IFunction;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SingularFormException;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.converter.SimpleSInstanceConverter;
import org.apache.wicket.model.IModel;

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
    public SInstance getMInstancia() {
        return model.getObject();
    }

    @Override
    public Serializable getObject() {
        if (model.getObject().isEmptyOfData()) {
            return null;
        }
        if (resolver.apply(getMInstancia()).isPresent()) {
            return resolver.apply(getMInstancia()).get().toObject(model.getObject());
        } else {
            if (getMInstancia() instanceof SIComposite) {
                throw new SingularFormException("Nenhum converter foi informado para o tipo " + getMInstancia().getName());
            } else {
                return new SimpleSInstanceConverter<>().toObject(getMInstancia());
            }
        }
    }

    @Override
    public void setObject(Serializable object) {
        if (object == null) {
            getMInstancia().clearInstance();
        } else {
            if (resolver.apply(getMInstancia()).isPresent()) {
                resolver.apply(getMInstancia()).get().fillInstance(getMInstancia(), object);
            } else {
                if (getMInstancia() instanceof SIComposite) {
                    throw new SingularFormException("Nenhum converter foi informado para o tipo " + getMInstancia().getName());
                } else {
                    new SimpleSInstanceConverter<>().fillInstance(getMInstancia(), object);
                }
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
