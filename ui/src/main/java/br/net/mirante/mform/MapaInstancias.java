package br.net.mirante.mform;

import java.util.function.Function;

class MapaInstancias {

    private final Function<String, MTipo<?>> recuperadorDefinicao;

    MapaInstancias(Function<String, MTipo<?>> recuperadorDefinicao) {
        this.recuperadorDefinicao = recuperadorDefinicao;
    }
}
