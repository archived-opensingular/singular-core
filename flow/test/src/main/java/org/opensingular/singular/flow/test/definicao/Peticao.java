/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.test.definicao;

import java.util.Calendar;

import org.opensingular.flow.core.DefinitionInfo;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.MBPMUtil;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskPredicates;
import org.opensingular.flow.core.builder.BProcessRole;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.defaults.NullTaskAccessStrategy;

import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.AGUARDANDO_ANALISE;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.AGUARDANDO_GERENTE;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.AGUARDANDO_PUBLICACAO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.DEFERIDO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.EM_EXIGENCIA;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.INDEFERIDO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.NOTIFICAR_NOVA_INSTANCIA;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.PUBLICADO;

@DefinitionInfo("Peticoes")
public class Peticao extends ProcessDefinition<ProcessInstance> {

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

        PeticaoTask(String name) {
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

    // Papeis
    public static final String PAPEL_ANALISTA = "analista";
    public static final String PAPEL_GERENTE = "GERENTE";

    public Peticao() {
        super(ProcessInstance.class);
    }

    @Override
    protected FlowMap createFlowMap() {
        setName("Teste", "Petição");

        FlowBuilderImpl flow = new FlowBuilderImpl(this);

        BProcessRole<?> papelAnalista = flow.addRoleDefinition("ANALISTA", PAPEL_ANALISTA, false);
        BProcessRole<?> papelGerente = flow.addRoleDefinition("GERENTE", PAPEL_GERENTE, false);

        flow.addJavaTask(NOTIFICAR_NOVA_INSTANCIA).call(this::notificar);
        flow.addPeopleTask(AGUARDANDO_ANALISE, papelAnalista);
        flow.addPeopleTask(EM_EXIGENCIA, new NullTaskAccessStrategy());
        flow.addPeopleTask(AGUARDANDO_GERENTE, papelGerente)
                .withTargetDate((processInstance, taskInstance) -> addDias(processInstance, 1).getTime());
        flow.addPeopleTask(AGUARDANDO_PUBLICACAO, new NullTaskAccessStrategy());
        flow.addEnd(INDEFERIDO);
        flow.addEnd(DEFERIDO);
        flow.addEnd(PUBLICADO).addStartedTaskListener((taskIntance, execucaoTask) -> System.out.println(taskIntance.getName() + " Iniciado"));
        flow.setStartTask(NOTIFICAR_NOVA_INSTANCIA);

        flow.from(NOTIFICAR_NOVA_INSTANCIA).go(ENVIAR_PARA_ANALISE, AGUARDANDO_ANALISE);
        flow.from(AGUARDANDO_ANALISE).go(COLOCAR_EM_EXIGENCIA, EM_EXIGENCIA);
        flow.from(EM_EXIGENCIA).go(CUMPRIR_EXIGENCIA, AGUARDANDO_ANALISE);
        flow.from(AGUARDANDO_ANALISE).go(INDEFERIR, INDEFERIDO);
        flow.from(AGUARDANDO_ANALISE).go(APROVAR_TECNICO, AGUARDANDO_GERENTE);
        flow.from(AGUARDANDO_GERENTE).go(APROVAR_GERENTE, AGUARDANDO_PUBLICACAO);
        flow.from(AGUARDANDO_GERENTE).go(SOLICITAR_AJUSTE_ANALISE, AGUARDANDO_ANALISE);
        flow.from(AGUARDANDO_PUBLICACAO).go(PUBLICAR, PUBLICADO);
        flow.from(AGUARDANDO_GERENTE).go(DEFERIR, DEFERIDO);

        // Tarefa aguardando analise a mais de um dia é indeferida automaticamente
        flow.addAutomaticTransition(AGUARDANDO_ANALISE, TaskPredicates.timeLimitInDays(1), INDEFERIDO);
        flow.addAutomaticTransition(AGUARDANDO_GERENTE, TaskPredicates.timeLimitInDays(1), AGUARDANDO_PUBLICACAO);

        return flow.build();
    }

    private Calendar addDias(Object taskInstance, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(((ProcessInstance) taskInstance).getBeginDate());
        calendar.add(Calendar.DATE, dias);
        return calendar;
    }

    public void notificar(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        System.out.println("Notificado");

    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        MBPMUtil.showSwingDiagram((Class<? extends ProcessDefinition<?>>) new Object() {
            /* VAZIO */
        }.getClass().getEnclosingClass());
    }
}
