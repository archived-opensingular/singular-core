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

package org.opensingular.flow.test.support;

import com.mchange.util.AssertException;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.lib.commons.test.AssertionsBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe de apoio a construção de asertivas de teste para {@link FlowInstance}.
 *
 * @author Daniel C. Bordin on 18/03/2017.
 */
public class AssertionsFlowInstance extends AssertionsBase<FlowInstance, AssertionsFlowInstance> {

    public AssertionsFlowInstance(FlowInstance target) {
        super(target);
    }

    @Override
    protected String errorMsg(String msg) {
        return getTargetOpt().map(target -> "(flowInstance=" + target + ") " + msg).orElse(msg);
    }

    /**
     * Verifica se tarefa corrente é do tipo esperado, ou seja, se o fluxo está no estado esperado. Caso contrário,
     * dispara exception.
     */
    public AssertionsFlowInstance isAtTask(ITaskDefinition expectedCurrentTaskType) {
        currentTask().isAtTask(expectedCurrentTaskType);
        return this;
    }

    private AssertionsTaskInstance currentTask() {
        TaskInstance task = getTarget().getCurrentTask().orElseThrow(
                () -> new AssertException(errorMsg("Não há uma tarefa corrente (currentTask() == null)")));
        return new AssertionsTaskInstance(task);
    }

    /** Verifica se a váriavel do fluxo correspondente ao valor esperado. Caso contrário, dispara exception. */
    public AssertionsFlowInstance isVariableValue(@Nonnull String variableName, @Nullable Object expectedValue) {
        Object currentValue = getTarget().getVariables().getValue(variableName);
        if(!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("Valor incorreto na váriavel '" + variableName + "' do fluxo", expectedValue,
                            currentValue));
        }
        return this;
    }

    /**
     * Verifica se a lista de variáveis do fluxo corresponde a quantidade de variáveis e variáveis não nula
     * informada, caso contrário dispara exception.
     */
    public AssertionsFlowInstance isVariablesSize(int expectedListSize, int expectedNotNullSize) {
        if(getTarget().getVariables().size() != expectedListSize) {
            throw new AssertionError(errorMsg("Quantidade de variáveis não esperadas", expectedListSize,
                    getTarget().getVariables().size()));
        }
        long countNotNull = getTarget().getVariables().asCollection().stream().filter(
                v -> v.getValue() != null).count();
        if (countNotNull != expectedNotNullSize) {
            throw new AssertionError(
                    errorMsg("Quantidade de variáveis diferentes de null não esperadas", expectedNotNullSize,
                            countNotNull));
        }
        return this;
    }

    /** Verifica se a descrição do fluxo corresponde a descrição esperada, caso contrário dispara exception. */
    public AssertionsFlowInstance isDescription(String expectedDescription) {
        if (!Objects.equals(expectedDescription, getTarget().getDescription())) {
            throw new AssertionError(errorMsg("Flow description diferent of the expected", expectedDescription,
                    getTarget().getDescription()));
        }
        return this;
    }

    /**
     * Verifica se o fluxo possui um histórico de tarefa compatível com a sequencia informada.
     */
    public AssertionsFlowInstance isTaskSequence(ITaskDefinition... expectedTaskSequence) {
        List<TaskInstance> tasks = getTarget().getTasksOlderFirst();
        int pos = -1;
        if (tasks.size() == expectedTaskSequence.length) {
            for(int i = 0 ; i < expectedTaskSequence.length; i++) {
                if (! tasks.get(i).isAtTask(expectedTaskSequence[i])) {
                    pos = i;
                    break;
                }
            }
        } else {
            pos = Math.min(tasks.size(), expectedTaskSequence.length);
        }
        if (pos != -1) {
            String expected = Arrays.stream(expectedTaskSequence).map(e -> e.toString()).collect(
                    Collectors.joining(", "));
            String current = tasks.stream().map(t -> t.getFlowTask().get().getAbbreviation()).collect(
                    Collectors.joining(", "));
            throw new AssertionError(errorMsg("Sequencia de Tasks diferente na posição " + pos, expected,current));
        }
        return this;
    }
}
