package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanciaItemListaModel<I extends MInstancia> extends AbstractMInstanciaItemListaModel<I> {

    private int index;

    public MInstanciaItemListaModel(Object rootTarget, int index) {
        super(rootTarget);
        this.index = index;
    }

    @Override
    protected int index() {
        return index;
    }
}
