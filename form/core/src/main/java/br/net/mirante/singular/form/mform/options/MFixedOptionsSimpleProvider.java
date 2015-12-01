package br.net.mirante.singular.form.mform.options;

import java.util.Arrays;
import java.util.Collection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;

public class MFixedOptionsSimpleProvider implements MOptionsProvider {

    private final MILista<? extends MInstancia> opcoes;

    public MFixedOptionsSimpleProvider(MTipoSimples<?, ?> tipoOpcoes, Collection<? extends Object> lista) {
        if (lista.isEmpty()) {
            throwEmpryListError();
        }
        this.opcoes = tipoOpcoes.novaLista();
        lista.forEach(o -> opcoes.addValor(o));
    }

    public MFixedOptionsSimpleProvider(MTipoSimples<?, ?> tipoOpcoes, Object[] lista) {
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
    public MILista<? extends MInstancia> getOpcoes(MInstancia optionsInstance) {
        return opcoes;
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
