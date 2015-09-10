package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;

public class MInstanciaValorModel<T>
    implements
    IModel<T>,
    IObjectClassAwareModel<T> {

    private IModel<? extends MInstancia> instanciaModel;

    public MInstanciaValorModel(IModel<? extends MInstancia> instanciaModel) {
        this.instanciaModel = instanciaModel;
    }

    public MInstancia getTarget() {
        return instanciaModel.getObject();
    }

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

    @Override
    public void detach() {
        this.instanciaModel.detach();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanciaModel == null) ? 0 : instanciaModel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MInstanciaValorModel<?> other = (MInstanciaValorModel<?>) obj;
        if (instanciaModel == null) {
            if (other.instanciaModel != null)
                return false;
        } else if (!instanciaModel.equals(other.instanciaModel))
            return false;
        return true;
    }
}