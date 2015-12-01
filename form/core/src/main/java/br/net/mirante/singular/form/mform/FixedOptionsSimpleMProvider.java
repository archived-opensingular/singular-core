package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.Collection;

class FixedOptionsSimpleMProvider implements MProviderOpcoes {

    private final MILista<? extends MInstancia> opcoes;

    public FixedOptionsSimpleMProvider(MTipoSimples<?, ?> tipoOpcoes, Collection<? extends Object> lista) {
        if (lista.isEmpty()) {
            throwEmpryListError();
        }
        this.opcoes = tipoOpcoes.novaLista();
        lista.forEach(o -> opcoes.addValor(o));
    }

    public FixedOptionsSimpleMProvider(MTipoSimples<?, ?> tipoOpcoes, Object[] lista) {
        if (lista.length == 0) {
            throwEmpryListError();
        }
        this.opcoes = tipoOpcoes.novaLista();
        Arrays.stream(lista).forEach(o -> opcoes.addValor(o));
    }

    private void throwEmpryListError() {
        throw new RuntimeException("Empty list is not valid as options.");
    }

    @Override
    public MILista<? extends MInstancia> getOpcoes() {
        return opcoes;
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
