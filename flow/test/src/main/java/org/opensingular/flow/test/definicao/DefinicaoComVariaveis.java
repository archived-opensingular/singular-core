/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.definicao;

import org.opensingular.flow.core.DefinitionInfo;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.builder.FlowBuilder;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.variable.VarDefinitionImpl;
import org.opensingular.flow.core.variable.type.VarTypeDecimal;
import org.opensingular.flow.core.variable.type.VarTypeString;

import java.math.BigDecimal;

@DefinitionInfo("DefVar")
public class DefinicaoComVariaveis extends ProcessDefinition<ProcessInstance> {

    public static final BigDecimal BIGDECIMAL_USADO_NO_TESTE = new BigDecimal("1111111123242343240.00001E-3");
    public static final String STRING_USADA_NO_TESTE = "Pessoa X";

    public DefinicaoComVariaveis() {
        super(ProcessInstance.class);
        getVariables().addVariable(new VarDefinitionImpl("nome", "Nome de Alguém", new VarTypeString(), false));
        getVariables().addVariable(new VarDefinitionImpl("qualquerCoisa", "Qualquer Coisa Numerica", new VarTypeDecimal(), false));
    }

    @Override
    protected FlowMap createFlowMap() {
        FlowBuilder f = new FlowBuilderImpl(this);

        ITaskDefinition PRINT = () -> "Print Variavel";
        f.addJavaTask(PRINT).call(this::printVar);

        ITaskDefinition SET_VARIAVEL = () -> "Definir Variavel";
        f.addJavaTask(SET_VARIAVEL).call(this::setVar);

        ITaskDefinition APROVAR = () -> "Aprovar Definiçâo";
        f.addJavaTask(APROVAR).call(this::print);

        ITaskDefinition END = () -> "Aprovado";
        f.addEnd(END);

        f.setStartTask(SET_VARIAVEL);
        f.from(SET_VARIAVEL).go(APROVAR);
        f.from(APROVAR).go(PRINT);
        f.from(PRINT).go(END);

        return f.build();
    }

    public ProcessInstance start() {
        ProcessInstance instancia = newInstance();
        instancia.start();
        return instancia;
    }

    public void print(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        System.out.println("legal");
    }

    public void setVar(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        instancia.setVariavel("nome", STRING_USADA_NO_TESTE);
        instancia.setVariavel("qualquerCoisa", BIGDECIMAL_USADO_NO_TESTE);

        instancia.saveEntity();
    }

    public void printVar(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        System.out.println("########### nome          #####>" + instancia.getValorVariavel("nome"));
        System.out.println("########### qualquerCoisa #####>" + instancia.getValorVariavel("qualquerCoisa"));
    }
}
