package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanciaModel<T> extends AbstractMInstanciaModel<T> {

    private Object target;

    public MInstanciaModel(Object target) {
        this.target = target;
    }

    @Override
    public MInstancia getTarget() {
        return (MInstancia) ((target instanceof IModel<?>)
            ? ((IModel<?>) target).getObject()
            : target);
    }
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void detach() {
        if (target instanceof IDetachable) {
            ((IDetachable) target).detach();
        }
    }
}
