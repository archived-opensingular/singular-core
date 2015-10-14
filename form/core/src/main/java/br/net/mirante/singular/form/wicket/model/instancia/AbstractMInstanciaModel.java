package br.net.mirante.singular.form.wicket.model.instancia;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public abstract class AbstractMInstanciaModel<I extends MInstancia>
    implements IModel<I>, IMInstanciaAwareModel<I> {

    @Override
    public final MInstancia getMInstancia() {
        return getObject();
    }

    @Override
    public void setObject(I object) {
        throw new UnsupportedOperationException("Model " + getClass() +
            " does not support setObject(Object)");
    }

    @Override
    public void detach() {}
}
