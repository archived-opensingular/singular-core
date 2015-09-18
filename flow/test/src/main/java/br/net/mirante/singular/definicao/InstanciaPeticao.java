package br.net.mirante.singular.definicao;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;

public class InstanciaPeticao extends ProcessInstance {

    public InstanciaPeticao() {
        super(Peticao.class);
    }

    public InstanciaPeticao(IEntityProcessInstance instance) {
        super(Peticao.class, instance);
    }
}
