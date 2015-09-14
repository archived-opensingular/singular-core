package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;

public class InstanciaDefinicao extends ProcessInstance {

    public InstanciaDefinicao() {
        super(Definicao.class);
    }

    public InstanciaDefinicao(IEntityProcessInstance instance) {
        super(Definicao.class, instance);
    }
}
