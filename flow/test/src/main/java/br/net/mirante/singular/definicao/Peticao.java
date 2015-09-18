package br.net.mirante.singular.definicao;

import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.defaults.DefaultPageStrategy;
import br.net.mirante.singular.defaults.DefaultTaskAccessStrategy;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BPeople;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;

public class Peticao extends ProcessDefinition<InstanciaPeticao> {

    public Peticao() {
        super(InstanciaPeticao.class);
    }

    @Override
    protected FlowMap createFlowMap() {
        setName("Teste", "Petição");

        FlowBuilder flow = new FlowBuilderImpl(this);

        BPeople aguardandoAnalise = flow.addPeopleTask(() -> "Aguardando análise", new DefaultTaskAccessStrategy());
        aguardandoAnalise.withExecutionPage(new DefaultPageStrategy());
        BPeople emExigencia = flow.addPeopleTask(() -> "Em exigência", new DefaultTaskAccessStrategy());
        emExigencia.withExecutionPage(new DefaultPageStrategy());
        BPeople indeferido = flow.addPeopleTask(() -> "Indeferido", new DefaultTaskAccessStrategy());
        indeferido.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoGerente = flow.addPeopleTask(() -> "Aguardando gerente", new DefaultTaskAccessStrategy());
        aguardandoGerente.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoPublicacao = flow.addPeopleTask(() -> "Aguardando publicação", new DefaultTaskAccessStrategy());
        aguardandoPublicacao.withExecutionPage(new DefaultPageStrategy());
        BEnd deferido = flow.addEnd(() -> "Deferido");
        BEnd publicado = flow.addEnd(() -> "Publicado");

        flow.setStartTask(aguardandoAnalise);

        flow.addTransition(aguardandoAnalise, emExigencia);

        return flow.build();
    }

}
