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
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.lib.commons.test.AssertionsBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Classe de apoio a construção de asertivas de teste para {@link ProcessInstance}.
 *
 * @author Daniel C. Bordin on 18/03/2017.
 */
public class AssertionsProcessInstance extends AssertionsBase<ProcessInstance, AssertionsProcessInstance> {

    public AssertionsProcessInstance(ProcessInstance target) {
        super(target);
    }

    public AssertionsProcessInstance(Optional<? extends ProcessInstance> target) {
        super(target);
    }

    @Override
    protected String errorMsg(String msg) {
        Optional<ProcessInstance> target = getTargetOpt();
        if (target.isPresent()) {
            return "(processInstance=" + target.get() + ") " + msg;
        }
        return msg;
    }

    /**
     * Verifica se tarefa corrente é do tipo esperado, ou seja, se o processo está no estado esperado. Caso contrário,
     * dispara exception.
     */
    public AssertionsProcessInstance isAtTask(ITaskDefinition expectedCurrentTaskType) {
        currentTask().isAtTask(expectedCurrentTaskType);
        return this;
    }

    private AssertionsTaskInstance currentTask() {
        TaskInstance task = getTarget().getCurrentTask().orElseThrow(
                () -> new AssertException(errorMsg("Não há uma tarefa corrente (currentTask() == null)")));
        return new AssertionsTaskInstance(task);
    }

    /** Verifica se a váriavel do processo correspondente ao valor esperado. Caso contrário, dispara exception. */
    public AssertionsProcessInstance isVariableValue(@Nonnull String variableName, @Nullable Object expectedValue) {
        Object currentValue = getTarget().getVariables().getValue(variableName);
        if(!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("Valor incorreto na váriavel '" + variableName + "' do processo", expectedValue,
                            currentValue));
        }
        return this;
    }

    /**
     * Verifica se a lista de variáveis do processo corresponde a quantidade de variáveis e variáveis não nula
     * informada, caso contrário dispara exception.
     */
    public AssertionsProcessInstance isVariablesSize(int expectedListSize, int expectedNotNullSize) {
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
}
