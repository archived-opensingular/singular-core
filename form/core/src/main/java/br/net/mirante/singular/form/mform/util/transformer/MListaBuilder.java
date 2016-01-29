package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class MListaBuilder<T extends STypeComposto> {

    private SList<?> sList;
    private T mTipo;

    /**
     * Instancia do tipo dos elementos da lista
     *
     * @param mtipo
     */
    public MListaBuilder(T mtipo) {
        this.mTipo = mtipo;
        this.sList = mtipo.novaLista();
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
     *
     * @return
     */
    public MCompostoValueSetter add() {
        SIComposite novaInstancia = (SIComposite) mTipo.novaInstancia();
        sList.addElement(novaInstancia);
        return new MCompostoValueSetter(novaInstancia, this);
    }

    public SList<?> getList() {
        return sList;
    }

    public static class MCompostoValueSetter {
        private MListaBuilder _lb;
        private SIComposite instancia;

        public MCompostoValueSetter(SIComposite instancia, MListaBuilder lb) {
            this._lb = lb;
            this.instancia = instancia;
        }

        public MCompostoValueSetter set(SType<?> tipo, Object value) {
            instancia.setValor(tipo, value);
            return this;
        }

        public MCompostoValueSetter set(String path, Object value) {
            instancia.setValor(path, value);
            return this;
        }

        public MCompostoValueSetter add() {
            return _lb.add();
        }

        public SList<?> getList() {
            return _lb.getList();
        }
    }

}
