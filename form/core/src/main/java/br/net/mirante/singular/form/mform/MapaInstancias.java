package br.net.mirante.singular.form.mform;

import java.util.function.Function;

class MapaInstancias {

    private final Function<String, SType<?>> recuperadorDefinicao;

    MapaInstancias(Function<String, SType<?>> recuperadorDefinicao) {
        this.recuperadorDefinicao = recuperadorDefinicao;
    }
}
