/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.test.definicao;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.builder.BProcessRole;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.defaults.NullPageStrategy;

public class DefinicaoProcessVersoes extends ProcessDefinition<ProcessVersoes> {

    public DefinicaoProcessVersoes() {
        super("DefVersoes",ProcessVersoes.class);
    }

    public static InstanceProcessVersao flow = InstanceProcessVersao.VERSAO_1;

    @Override
    protected FlowMap createFlowMap() {
        return flow.createFlowMap(this);
    }

    public static void changeFlowToVersao1() {
        flow = InstanceProcessVersao.VERSAO_1;
    }

    public static void changeFlowToVersao1ComPapeis() {
        flow = InstanceProcessVersao.VERSAO_1_COM_PAPEIS;
    }

    public static void changeFlowToVersao2() {
        flow = InstanceProcessVersao.VERSAO_2;
    }

    private enum InstanceProcessVersao {
        VERSAO_1() {
            @Override
            public FlowMap createFlowMap(DefinicaoProcessVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BProcessRole<?> papelTecnico = flow.addRoleDefinition("TECNICO", "TECNICO", false);

                ITaskDefinition start = () -> "Start";
                flow.addJavaTask(start).call((ProcessVersoes p) -> {});
                ITaskDefinition task = () -> "Task";
                flow.addJavaTask(task).call((ProcessVersoes p) -> {});
                ITaskDefinition people = () -> "People";
                flow.addPeopleTask(people, papelTecnico).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End";
                flow.addEnd(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        },

        VERSAO_1_COM_PAPEIS() {
            @Override
            public FlowMap createFlowMap(DefinicaoProcessVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BProcessRole<?> papelAnalista = flow.addRoleDefinition("ANALISTA", "ANALISTA", false);

                ITaskDefinition start = () -> "Start";
                flow.addJavaTask(start).call((ProcessVersoes p) -> {});
                ITaskDefinition task = () -> "Task";
                flow.addJavaTask(task).call((ProcessVersoes p) -> {});
                ITaskDefinition people = () -> "People";
                flow.addPeopleTask(people, papelAnalista).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End";
                flow.addEnd(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        },

        VERSAO_2() {
            @Override
            public FlowMap createFlowMap(DefinicaoProcessVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BProcessRole<?> papelTecnico = flow.addRoleDefinition("TECNICO", "TECNICO", false);

                ITaskDefinition start = () -> "Start 2";
                flow.addJavaTask(start).call((ProcessVersoes p) -> {});
                ITaskDefinition task = () -> "Task 2";
                flow.addJavaTask(task).call((ProcessVersoes p) -> {});
                ITaskDefinition people = () -> "People 2";
                flow.addPeopleTask(people, papelTecnico).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End 2";
                flow.addEnd(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        };

        public abstract FlowMap createFlowMap(DefinicaoProcessVersoes definicaoProcessVersoes);
    }

}
