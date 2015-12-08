package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IMInstanciaAwareModel<T> extends IModel<T> {
    MInstancia getMInstancia();

    public static IModel<MInstancia> getInstanceModel(IMInstanciaAwareModel<?> model) {
        return new IMInstanciaAwareModel<MInstancia>() {
            public MInstancia getObject() {
                return getMInstancia();
            }
            public MInstancia getMInstancia() {
                return model.getMInstancia();
            }
            @Override
            public void setObject(MInstancia object) {}
            @Override
            public void detach() {}
        };
    }
}
