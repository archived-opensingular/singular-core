package br.net.mirante.singular.form.wicket;

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

}
