package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class MListaBuilder<T extends MTipoComposto> {

    private MILista<?> miLista;
    private T mTipo;

    /**
     * Instancia do tipo dos elementos da lista
     *
     * @param mtipo
     */
    public MListaBuilder(T mtipo) {
        this.mTipo = mtipo;
        this.miLista = mtipo.novaLista();
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
     *
     * @return
     */
    public MCompostoValueSetter add() {
        MIComposto novaInstancia = (MIComposto) mTipo.novaInstancia();
        miLista.addElement(novaInstancia);
        return new MCompostoValueSetter(novaInstancia, this);
    }

    public MILista<?> getList() {
        return miLista;
    }

    public static class MCompostoValueSetter {
        private MListaBuilder _lb;
        private MIComposto instancia;

        public MCompostoValueSetter(MIComposto instancia, MListaBuilder lb) {
            this._lb = lb;
            this.instancia = instancia;
        }

        public MCompostoValueSetter set(MTipo<?> tipo, Object value) {
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

        public MILista<?> getList() {
            return _lb.getList();
        }
    }

}
