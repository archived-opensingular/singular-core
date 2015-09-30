package br.net.mirante.singular.definicao;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;

public class ProcessVersoes extends ProcessInstance {

    public ProcessVersoes() {
        super(InstanceProcessVersoes.class);
    }

    public ProcessVersoes(IEntityProcessInstance instance) {
        super(InstanceProcessVersoes.class, instance);
    }
}
