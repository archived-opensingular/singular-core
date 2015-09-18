package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;

public class InstanciaDefinicaoComVariavel extends ProcessInstance {

    public InstanciaDefinicaoComVariavel() {
        super(DefinicaoComVariaveis.class);
    }

    public InstanciaDefinicaoComVariavel(IEntityProcessInstance instance) {
        super(DefinicaoComVariaveis.class, instance);
    }
}
