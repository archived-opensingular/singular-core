package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BJava;
import br.net.mirante.singular.flow.core.builder.BTask;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;

public class Definicao extends ProcessDefinition<InstanciaDefinicao> {

    public Definicao() {
        super(InstanciaDefinicao.class);
    }


    @Override
    protected FlowMap createFlowMap() {
        FlowBuilder flow = new FlowBuilderImpl(this);


        BJava task = flow.addJavaTask(() -> "Aprovar Definiçâo");
        task.call(this::print);
        flow.setStartTask(task);
        BEnd end = flow.addEnd(() -> "Aprovado");
        flow.addTransition(task, end);

        return flow.build();
    }

    public void print(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
        System.out.println("legal");
    }

}
