package br.net.mirante.singular.definicao;

import br.net.mirante.singular.defaults.DefaultPageStrategy;
import br.net.mirante.singular.defaults.DefaultTaskAccessStrategy;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BPeople;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;

public class Peticao extends ProcessDefinition<InstanciaPeticao> {

    public static final String COLOCAR_EM_EXIGENCIA = "Colocar em exigência";
    public static final String INDEFERIR = "Indeferir";
    public static final String APROVAR_TECNICO = "Aprovar técnico";
    public static final String APROVAR_GERENTE = "Aprovar gerente";
    public static final String PUBLICAR = "Publicar";
    public static final String DEFERIR = "Deferir";
    public static final String CUMPRIR_EXIGENCIA = "Cumprir exigência";
    public static final String SOLICITAR_AJUSTE_ANALISE = "Solicitar ajuste análise técnica";

    public Peticao() {
        super(InstanciaPeticao.class);
    }

    @Override
    protected FlowMap createFlowMap() {
        setName("Teste", "Petição");

        FlowBuilder flow = new FlowBuilderImpl(this);

        BPeople aguardandoAnalise = flow.addPeopleTask(() -> "Aguardando análise", new DefaultTaskAccessStrategy());
        aguardandoAnalise.withExecutionPage(new DefaultPageStrategy());
        BPeople emExigencia = flow.addPeopleTask(() -> "Em exigência - Setor regulado", new DefaultTaskAccessStrategy());
        emExigencia.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoGerente = flow.addPeopleTask(() -> "Aguardando gerente", new DefaultTaskAccessStrategy());
        aguardandoGerente.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoPublicacao = flow.addPeopleTask(() -> "Aguardando publicação", new DefaultTaskAccessStrategy());
        aguardandoPublicacao.withExecutionPage(new DefaultPageStrategy());
        BEnd indeferido = flow.addEnd(() -> "Indeferido");
        BEnd deferido = flow.addEnd(() -> "Deferido");
        BEnd publicado = flow.addEnd(() -> "Publicado");

        flow.setStartTask(aguardandoAnalise);

        flow.addTransition(aguardandoAnalise, COLOCAR_EM_EXIGENCIA, emExigencia);
        flow.addTransition(emExigencia, CUMPRIR_EXIGENCIA, aguardandoAnalise);
        flow.addTransition(aguardandoAnalise, INDEFERIR, indeferido);
        flow.addTransition(aguardandoAnalise, APROVAR_TECNICO, aguardandoGerente);
        flow.addTransition(aguardandoGerente, APROVAR_GERENTE, aguardandoPublicacao);
        flow.addTransition(aguardandoGerente, SOLICITAR_AJUSTE_ANALISE, aguardandoAnalise);
        flow.addTransition(aguardandoPublicacao, PUBLICAR, publicado);
        flow.addTransition(aguardandoGerente, DEFERIR, deferido);

        return flow.build();
    }

}
