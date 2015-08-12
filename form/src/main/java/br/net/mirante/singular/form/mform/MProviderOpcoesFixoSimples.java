package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.Collection;

class MProviderOpcoesFixoSimples implements MProviderOpcoes {

    private final MILista opcoes;

    public MProviderOpcoesFixoSimples(MTipoSimples<?, ?> tipoOpcoes, Collection<? extends Object> lista) {
        if (lista.isEmpty()) {
            throw new RuntimeException("Não é aceito uma lista de opções tamanho zero");
        }
        this.opcoes = tipoOpcoes.novaLista();
        lista.forEach(o -> opcoes.addValor(o));
    }

    public MProviderOpcoesFixoSimples(MTipoSimples<?, ?> tipoOpcoes, Object[] lista) {
        if (lista.length == 0) {
            throw new RuntimeException("Não é aceito uma lista de opções tamanho zero");
        }
        this.opcoes = tipoOpcoes.novaLista();
        Arrays.stream(lista).forEach(o -> opcoes.addValor(o));
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
