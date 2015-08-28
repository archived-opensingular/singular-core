package br.net.mirante.singular.form.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.model.IComponentInheritedModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import br.net.mirante.singular.form.mform.MInstancia;

public class MICompostoModel<T> extends AbstractMInstanciaModel<T>
    implements IComponentInheritedModel<T> {

    private Object target;

    public MICompostoModel(Object target) {
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

    public String propertyExpression(Component owner) {
        return owner.getId();
    }

    @Override
    public <W> IWrapModel<W> wrapOnInheritance(Component component) {
        return new AttachedCompoundPropertyModel<>(component);
    }
    public <S> IModel<S> bind(String property) {
        return new AbstractMInstanciaCampoModel<S>(this) {
            @Override
            protected String propertyExpression() {
                return property;
            }
        };
    }

    private class AttachedCompoundPropertyModel<C> extends AbstractMInstanciaCampoModel<C>
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
