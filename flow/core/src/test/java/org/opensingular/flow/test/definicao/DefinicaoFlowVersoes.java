/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.test.definicao;

import org.opensingular.flow.core.DefinitionInfo;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.builder.BuilderBusinessRole;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.defaults.NullPageStrategy;

@DefinitionInfo("DefVersoes")
public class DefinicaoFlowVersoes extends FlowDefinition<FlowVersoes> {

    public DefinicaoFlowVersoes() {
        super(FlowVersoes.class);
    }

    private static InstanceProcessVersao flow = InstanceProcessVersao.VERSAO_1;

    @Override
    protected FlowMap createFlowMap() {
        return flow.createFlowMap(this);
    }

    public synchronized static void  changeFlowToVersao1() {
        flow = InstanceProcessVersao.VERSAO_1;
    }

    public synchronized static void changeFlowToVersao1ComPapeis() {
        flow = InstanceProcessVersao.VERSAO_1_COM_PAPEIS;
    }

    public synchronized static void changeFlowToVersao2() {
        flow = InstanceProcessVersao.VERSAO_2;
    }

    private enum InstanceProcessVersao {
        VERSAO_1() {
            @Override
            public FlowMap createFlowMap(DefinicaoFlowVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BuilderBusinessRole<?> papelTecnico = flow.addRoleDefinition("TECNICO", "TECNICO", false);

                ITaskDefinition start = () -> "Start";
                flow.addJavaTask(start).call(p -> {

                });
                ITaskDefinition task = () -> "Task";
                flow.addJavaTask(task).call(p -> {

                });
                ITaskDefinition people = () -> "People";
                flow.addHumanTask(people, papelTecnico).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End";
                flow.addEndTask(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        },

        VERSAO_1_COM_PAPEIS() {
            @Override
            public FlowMap createFlowMap(DefinicaoFlowVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BuilderBusinessRole<?> papelAnalista = flow.addRoleDefinition("ANALISTA", "ANALISTA", false);

                ITaskDefinition start = () -> "Start";
                flow.addJavaTask(start).call(p -> {

                });
                ITaskDefinition task = () -> "Task";
                flow.addJavaTask(task).call(p -> {
                });
                ITaskDefinition people = () -> "People";
                flow.addHumanTask(people, papelAnalista).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End";
                flow.addEndTask(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        },

        VERSAO_2() {
            @Override
            public FlowMap createFlowMap(DefinicaoFlowVersoes definicaoProcessVersoes) {
                definicaoProcessVersoes.setName("Versão", "Usando versões");

                FlowBuilderImpl flow = new FlowBuilderImpl(definicaoProcessVersoes);

                BuilderBusinessRole<?> papelTecnico = flow.addRoleDefinition("TECNICO", "TECNICO", false);

                ITaskDefinition start = () -> "Start 2";
                flow.addJavaTask(start).call(p -> {

                });
                ITaskDefinition task = () -> "Task 2";
                flow.addJavaTask(task).call(p -> {

                });
                ITaskDefinition people = () -> "People 2";
                flow.addHumanTask(people, papelTecnico).withExecutionPage(new NullPageStrategy());
                ITaskDefinition end = () -> "End 2";
                flow.addEndTask(end);

                flow.setStartTask(start);
                flow.from(start).go(task);
                flow.from(task).go(people);
                flow.from(people).go(end);

                return flow.build();
            }
        };

        public abstract FlowMap createFlowMap(DefinicaoFlowVersoes definicaoProcessVersoes);
    }

}
