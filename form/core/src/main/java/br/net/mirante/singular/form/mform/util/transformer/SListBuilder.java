package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class SListBuilder<T extends STypeComposite> {

    private SIList<?> sList;
    private T mTipo;

    /**
     * Instancia do tipo dos elementos da lista
     *
     * @param mtipo
     */
    public SListBuilder(T mtipo) {
        this.mTipo = mtipo;
        this.sList = mtipo.newList();
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
     *
     * @return
     */
    public SCompositeValueSetter add() {
        SIComposite novaInstancia = (SIComposite) mTipo.newInstance();
        sList.addElement(novaInstancia);
        return new SCompositeValueSetter(novaInstancia, this);
    }

    public SIList<?> getList() {
        return sList;
    }

    public static class SCompositeValueSetter {
        private SListBuilder _lb;
        private SIComposite instancia;

        public SCompositeValueSetter(SIComposite instancia, SListBuilder lb) {
            this._lb = lb;
            this.instancia = instancia;
        }

        public SCompositeValueSetter set(SType<?> tipo, Object value) {
            instancia.setValue(tipo, value);
            return this;
        }

        public SCompositeValueSetter set(String path, Object value) {
            instancia.setValue(path, value);
            return this;
        }

        public SCompositeValueSetter add() {
            return _lb.add();
        }

        public SIList<?> getList() {
            return _lb.getList();
        }
    }

}
