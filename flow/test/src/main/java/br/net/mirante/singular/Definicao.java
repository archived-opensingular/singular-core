package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;

public class Definicao extends ProcessDefinition {

    protected Definicao(Class instanceClass) {
        super(instanceClass);
    }

    @Override
    protected FlowMap createFlowMap() {
        FlowBuilder flow = new FlowBuilderImpl(this);

        flow.addPeopleTask("Solicitar definição");
        flow.addJavaTask("Aprovar Definiçâo")
                .call(this::print);
        flow.addEnd("Aprovado");

        return flow.build();
    }

    public void print(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
        System.out.println("legal");
    }

}
