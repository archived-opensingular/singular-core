package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;

public abstract class AbstractMInstanciaModel<T>
    implements
    IModel<T>,
    IObjectClassAwareModel<T> {

    public abstract MInstancia getTarget();

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) getTarget().getValor();
    }

    @Override
    public void setObject(T object) {
        getTarget().setValor(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getObjectClass() {
        MTipo<?> mtipo = getTarget().getMTipo();
        if (mtipo instanceof MTipoSimples<?, ?>) {
            return (Class<T>) ((MTipoSimples<?, ?>) mtipo).getClasseTipoNativo();
        }
        return (Class<T>) mtipo.getClasseInstancia();
    }
}