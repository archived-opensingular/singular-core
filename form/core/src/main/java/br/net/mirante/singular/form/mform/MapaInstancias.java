package br.net.mirante.singular.form.mform;

import java.util.function.Function;

class MapaInstancias {

    private final Function<String, MTipo<?>> recuperadorDefinicao;

    MapaInstancias(Function<String, MTipo<?>> recuperadorDefinicao) {
        this.recuperadorDefinicao = recuperadorDefinicao;
    }
}
