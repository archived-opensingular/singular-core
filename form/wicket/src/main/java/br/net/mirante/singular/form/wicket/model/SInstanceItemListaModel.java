package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.SInstance;

public class SInstanceItemListaModel<I extends SInstance>
    extends AbstractSInstanceItemListaModel<I>
{

    private int index;

    public SInstanceItemListaModel(Object rootTarget, int index) {
        super(rootTarget);
        this.index = index;
    }

    @Override
    protected int index() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
