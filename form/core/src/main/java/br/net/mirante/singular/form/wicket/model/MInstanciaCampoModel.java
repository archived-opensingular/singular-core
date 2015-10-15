package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanciaCampoModel<I extends MInstancia> extends AbstractMInstanciaCampoModel<I> {

    private String propertyExpression;

    public MInstanciaCampoModel(Object rootTarget, String propertyExpression) {
        super(rootTarget);
        this.propertyExpression = propertyExpression;
    }

    @Override
    protected String propertyExpression() {
        return propertyExpression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((propertyExpression == null) ? 0 : propertyExpression.hashCode());
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
        MInstanciaCampoModel<?> other = (MInstanciaCampoModel<?>) obj;
        if (propertyExpression == null) {
            if (other.propertyExpression != null)
                return false;
        } else if (!propertyExpression.equals(other.propertyExpression))
            return false;
        return super.equals(obj);
    }

}
