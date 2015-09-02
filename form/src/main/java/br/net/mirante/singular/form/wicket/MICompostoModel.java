package br.net.mirante.singular.form.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentInheritedModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;

public class MICompostoModel<T extends MIComposto>
    extends AbstractReadOnlyModel<T>
    implements IComponentInheritedModel<T> {

    private Object target;

    public MICompostoModel(Object target) {
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) ((target instanceof IModel<?>)
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

    public String propertyExpression(Component owner) {
        return owner.getId();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <W> IWrapModel<W> wrapOnInheritance(Component component) {
        return new AttachedCompoundPropertyModel(component);
    }
    public <S extends MInstancia> IModel<S> bind(String property) {
        return new MInstanciaCampoModel<S>(this, property);
    }

    private class AttachedCompoundPropertyModel<C extends MInstancia> extends AbstractMInstanciaCampoModel<C>
        implements IWrapModel<C> {
        private final Component owner;
        public AttachedCompoundPropertyModel(Component owner) {
            super(MICompostoModel.this);
            this.owner = owner;
        }
        @Override
        protected String propertyExpression() {
            return MICompostoModel.this.propertyExpression(owner);
        }
        @Override
        public IModel<T> getWrappedModel() {
            return MICompostoModel.this;
        }

        @Override
        public void detach() {
            super.detach();
            MICompostoModel.this.detach();
        }
    }
}
