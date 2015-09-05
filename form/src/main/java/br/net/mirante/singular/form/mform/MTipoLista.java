package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MFormTipo(nome = "MTipoLista", pacote = MPacoteCore.class)
public class MTipoLista<E extends MTipo<?>> extends MTipo<MILista<?>> {

    private E tipoElementos;

    @SuppressWarnings("unchecked")
    public MTipoLista() {
        super((Class<? extends MILista<?>>) MILista.class);
    }

    @Override
    public MILista<?> novaInstancia() {
        if (tipoElementos == null) {
            throw new RuntimeException("Não é possível instanciar o tipo '" + getNome()
                + "' pois o tipo da lista (o tipo de seus elementos) não foram definidos");
        }
        MILista<?> lista = new MILista<>();
        lista.setTipo(this);
        Integer tamanhoInicial = lista.as(AtrBasic.class).getTamanhoInicial();
        if (tamanhoInicial != null)
            for (int i = tamanhoInicial; i > 0; i--)
                lista.addNovo();
        return lista;
    }

    void setTipoElementos(E tipoElementos) {
        if (this.tipoElementos != null) {
            throw new RuntimeException("O tipo da lista já está definido");
        }
        this.tipoElementos = tipoElementos;
    }

    void setTipoElementosNovoTipoComposto(String nomeSimplesNovoTipoComposto) {
        MTipoComposto<?> tipo = extenderTipo(nomeSimplesNovoTipoComposto, MTipoComposto.class);
        setTipoElementos((E) tipo);
    }

    public E getTipoElementos() {
        return tipoElementos;
    }
}
