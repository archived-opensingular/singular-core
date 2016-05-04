package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.converter.SimpleSInstanceConverter;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class SelectMInstanceAwareModel extends AbstractMInstanceAwareModel<Serializable> {

    private static final long serialVersionUID = -4455601838581324870L;

    private final IModel<? extends SInstance> model;

    public SelectMInstanceAwareModel(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    public SInstance getMInstancia() {
        return model.getObject();
    }

    public SInstance getProviderMInstancia() {
        if (model instanceof SInstanceItemListaModel) {
            return ((SInstanceItemListaModel) model).getRootTarget();
        } else {
            return model.getObject();
        }
    }

    @Override
    public Serializable getObject() {
        if (model.getObject().isEmptyOfData()) {
            return null;
        }
        if (getProviderMInstancia().asAtrProvider().getConverter() != null) {
            return getProviderMInstancia().asAtrProvider().getConverter().toObject(model.getObject());
        } else {
            if (getProviderMInstancia() instanceof SIComposite) {
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
            if (getProviderMInstancia().asAtrProvider().getConverter() != null) {
                getProviderMInstancia().asAtrProvider().getConverter().fillInstance(getMInstancia(), object);
            } else {
                if (getProviderMInstancia() instanceof SIComposite) {
                    throw new SingularFormException("Nenhum converter foi informado para o tipo " + getMInstancia().getName());
                } else {
                    new SimpleSInstanceConverter<>().fillInstance(getMInstancia(), object);
                }
            }
        }
    }
}
