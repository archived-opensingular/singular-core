package br.net.mirante.singular.definicao;

import br.net.mirante.singular.defaults.DefaultPageStrategy;
import br.net.mirante.singular.defaults.DefaultTaskAccessStrategy;
import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.TaskPredicates;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BJava;
import br.net.mirante.singular.flow.core.builder.BPeople;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;

import java.util.Calendar;
import java.util.Date;

import static br.net.mirante.singular.definicao.Peticao.PeticaoTask.*;

public class Peticao extends ProcessDefinition<InstanciaPeticao> {

    public enum PeticaoTask implements ITaskDefinition {
        NOTIFICAR_NOVA_INSTANCIA("Notificar nova instância"),
        AGUARDANDO_ANALISE("Aguardando análise"),
        EM_EXIGENCIA("Em exigência"),
        AGUARDANDO_GERENTE("Aguardando gerente"),
        AGUARDANDO_PUBLICACAO("Aguardando publicação"),
        INDEFERIDO("Indeferido"),
        DEFERIDO("Deferido"),
        PUBLICADO("Publicado");

        private final String name;

        private PeticaoTask(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static final String ENVIAR_PARA_ANALISE = "Enviar para análise";
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

        FlowBuilderImpl flow = new FlowBuilderImpl(this);

        BJava notificarNovaInstancia = flow.addJavaTask(NOTIFICAR_NOVA_INSTANCIA).call(this::notificar);
        BPeople aguardandoAnalise = flow.addPeopleTask(AGUARDANDO_ANALISE, new DefaultTaskAccessStrategy());
        aguardandoAnalise.withExecutionPage(new DefaultPageStrategy());
        aguardandoAnalise.withTargetDate((processInstance, taskInstance) -> addDias(processInstance, 0).getTime());
        BPeople emExigencia = flow.addPeopleTask(EM_EXIGENCIA, new DefaultTaskAccessStrategy());
        emExigencia.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoGerente = flow.addPeopleTask(AGUARDANDO_GERENTE, new DefaultTaskAccessStrategy());
        aguardandoGerente.withExecutionPage(new DefaultPageStrategy());
        BPeople aguardandoPublicacao = flow.addPeopleTask(AGUARDANDO_PUBLICACAO, new DefaultTaskAccessStrategy());
        aguardandoPublicacao.withExecutionPage(new DefaultPageStrategy());
        BEnd indeferido = flow.addEnd(INDEFERIDO);
        BEnd deferido = flow.addEnd(DEFERIDO);
        BEnd publicado = flow.addEnd(PUBLICADO);
        flow.setStartTask(notificarNovaInstancia);

        flow.addTransition(notificarNovaInstancia, ENVIAR_PARA_ANALISE, aguardandoAnalise);
        flow.addTransition(aguardandoAnalise, COLOCAR_EM_EXIGENCIA, emExigencia);
        flow.addTransition(emExigencia, CUMPRIR_EXIGENCIA, aguardandoAnalise);
        flow.addTransition(aguardandoAnalise, INDEFERIR, indeferido);
        flow.addTransition(aguardandoAnalise, APROVAR_TECNICO, aguardandoGerente);
        flow.addTransition(aguardandoGerente, APROVAR_GERENTE, aguardandoPublicacao);
        flow.addTransition(aguardandoGerente, SOLICITAR_AJUSTE_ANALISE, aguardandoAnalise);
        flow.addTransition(aguardandoPublicacao, PUBLICAR, publicado);
        flow.addTransition(aguardandoGerente, DEFERIR, deferido);

        // Tarefa aguardando analise a mais de um dia é indeferida automaticamente
        flow.addAutomaticTransition(aguardandoAnalise, TaskPredicates.timeLimitInDays(1), indeferido);
        flow.addAutomaticTransition(aguardandoGerente, TaskPredicates.timeLimitInDays(0), aguardandoPublicacao);

        return flow.build();
    }

    private Calendar addDias(Object taskInstance, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(((ProcessInstance)taskInstance).getBeginDate());
        calendar.add(Calendar.DATE, dias);
        return calendar;
    }

    public void notificar(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
        System.out.println("Notificado");

    }

}
