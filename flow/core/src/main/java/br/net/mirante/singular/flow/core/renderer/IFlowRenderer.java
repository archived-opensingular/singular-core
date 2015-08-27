package br.net.mirante.singular.flow.core.renderer;

import br.net.mirante.singular.flow.core.ProcessDefinition;

public interface IFlowRenderer {
    byte[] generateImage(ProcessDefinition<?> definicao);
}
