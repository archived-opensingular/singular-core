package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;

public abstract class AbstractMInstanciaCampoModel<I extends MInstancia>
    extends AbstractReadOnlyModel<I>
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
}
