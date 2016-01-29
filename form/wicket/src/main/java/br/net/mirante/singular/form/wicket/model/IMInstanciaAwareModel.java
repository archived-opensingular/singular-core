package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance2;

public interface IMInstanciaAwareModel<T> extends IModel<T> {
    SInstance2 getMInstancia();

    public static IModel<SInstance2> getInstanceModel(IMInstanciaAwareModel<?> model) {
        return new IMInstanciaAwareModel<SInstance2>() {
            public SInstance2 getObject() {
                return getMInstancia();
            }
            public SInstance2 getMInstancia() {
                return model.getMInstancia();
            }
            @Override
            public void setObject(SInstance2 object) {}
            @Override
            public void detach() {}
        };
    }
}
