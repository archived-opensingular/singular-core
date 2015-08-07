package br.net.mirante.singular.flow.core.view;

import br.net.mirante.singular.flow.core.ProcessDefinition;

public interface GeradorBPMN {
    byte[] gerarBPMNImage(ProcessDefinition<?> definicao);
}
