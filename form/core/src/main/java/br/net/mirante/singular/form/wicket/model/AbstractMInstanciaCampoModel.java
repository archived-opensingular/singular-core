package br.net.mirante.singular.form.wicket.model;

import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;

public abstract class AbstractMInstanciaCampoModel<I extends MInstancia>
    extends AbstractMInstanciaModel<I>
    implements IChainingModel<I> {

    private Object rootTarget;

    public AbstractMInstanciaCampoModel(Object rootTarget) {
        this.rootTarget = rootTarget;
    }

    public String getPropertyExpression() {
        return propertyExpression();
    }

    protected abstract String propertyExpression();

    @Override
    @SuppressWarnings("unchecked")
    public I getObject() {
        return (I) getRootTarget().getCampo(getPropertyExpression());
    }

    public MIComposto getRootTarget() {
        return (MIComposto) ((rootTarget instanceof IModel<?>)
            ? ((IModel<?>) rootTarget).getObject()
            : rootTarget);
    }

    @Override
    public void detach() {
        if (rootTarget instanceof IDetachable) {
            ((IDetachable) rootTarget).detach();
        }
    }

    @Override
    public void setChainedModel(IModel<?> rootModel) {
        this.rootTarget = rootModel;
    }
    @Override
    public IModel<?> getChainedModel() {
        return (rootTarget instanceof IModel) ? (IModel<?>) rootTarget : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rootTarget == null) ? 0 : rootTarget.hashCode());
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
        AbstractMInstanciaCampoModel<?> other = (AbstractMInstanciaCampoModel<?>) obj;
        if (rootTarget == null) {
            if (other.rootTarget != null)
                return false;
        } else if (!rootTarget.equals(other.rootTarget))
            return false;
        return true;
    }
}
