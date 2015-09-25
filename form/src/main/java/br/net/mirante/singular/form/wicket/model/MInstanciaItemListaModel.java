package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanciaItemListaModel<I extends MInstancia>
    extends AbstractMInstanciaItemListaModel<I>
{

    private int index;

    public MInstanciaItemListaModel(Object rootTarget, int index) {
        super(rootTarget);
        this.index = index;
    }

    @Override
    protected int index() {
        return index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MInstanciaItemListaModel<?> other = (MInstanciaItemListaModel<?>) obj;
        if (index != other.index)
            return false;
        return true;
    }
}
