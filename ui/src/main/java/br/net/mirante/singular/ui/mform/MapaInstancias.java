package br.net.mirante.singular.ui.mform;

import java.util.function.Function;

class MapaInstancias {

    private final Function<String, MTipo<?>> recuperadorDefinicao;

    MapaInstancias(Function<String, MTipo<?>> recuperadorDefinicao) {
        this.recuperadorDefinicao = recuperadorDefinicao;
    }
}
