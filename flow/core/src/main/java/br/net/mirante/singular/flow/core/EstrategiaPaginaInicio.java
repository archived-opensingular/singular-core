package br.net.mirante.singular.flow.core;

import java.io.Serializable;

import br.net.mirante.singular.flow.util.view.Lnk;

@FunctionalInterface
public interface EstrategiaPaginaInicio extends Serializable {
    public Lnk getPaginaDestino(ProcessDefinition<?> definicaoProcesso, MUser user);
}
