/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.flow.core;

import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;

import javax.annotation.Nonnull;

/**
 * Objeto para a preparação para execução do início de um processo.
 *
 * @author Daniel C. Bordin on 20/03/2017.
 */
public final class StartCall<I extends ProcessInstance> extends CallWithParameters<StartCall<I>> {

    private final ProcessDefinition<I> processDefinition;

    private final MStart start;

    StartCall(ProcessDefinition<I> processDefinition, MStart start) {this.processDefinition = processDefinition;
        this.start = start;
        if (start.getFlowMap().getProcessDefinition() != processDefinition) {
            throw new SingularFlowException("Erro interno: processDefinition diferentes").add(
                    start.getFlowMap().getProcessDefinition()).add(processDefinition);
        }
    }

    /**
     * Cria a isntância do processo e dispara a execução do mesmo. Se existir, chama o código associado a inicialziação
     * do processo.
     */
    @Nonnull
    public I createAndStart() {
        return FlowEngine.createAndStart(this);
    }

    /**
     *  Verifica se os parâmetros atuais da chamada atende os requisitos da chamada.
     *  @see MStart#setStartValidator(MStart.IStartValidator)
     */
    @Override
    public ValidationResult validate() {
        ValidationResult result = super.validate();
        if (! result.hasErros() && getStart().getStartValidator() != null) {
            result = new ValidationResult();
            getStart().getStartValidator().validate((StartCall<ProcessInstance>) this, result);
        }
        return result;
    }

    @Override
    protected VarInstanceMap<?,?> newParameters() {
        return start.getParameters().newInstanceMap();
    }

    /** Definição desse ponto de inicialização segunda a definiçao do próprio processo. */
    public MStart getStart() {
        return start;
    }

    /** Retorna a definição do processo que será inicializado. */
    public ProcessDefinition<I> getProcessDefinition() {
        return processDefinition;
    }
}
