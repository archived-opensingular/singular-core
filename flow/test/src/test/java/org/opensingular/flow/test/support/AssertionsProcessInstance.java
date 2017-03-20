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
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.lib.commons.test.AssertionsBase;

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
        if (getTarget() != null) {
            return "(processInstance=" + getTarget() + ") " + msg;
        }
        return msg;
    }

    public AssertionsProcessInstance isAtTask(ITaskDefinition expectedCurrentTaskType) {
        currentTask().isAtTask(expectedCurrentTaskType);
        return this;
    }

    private AssertionsTaskInstance currentTask() {
        TaskInstance task = getTargetOrException().getCurrentTask().orElseThrow(
                () -> new AssertException(errorMsg("Não há uma tarefa corrente (currentTask() == null)")));
        return new AssertionsTaskInstance(task);
    }

    public AssertionsProcessInstance isVariableValue(String variableName, Object expectedValue) {
        Object currentValue = getTargetOrException().getVariables().getValue(variableName);
        if(!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("Valor incorreto na váriavel '" + variableName + "' do processo", expectedValue,
                            currentValue));
        }
        return this;
    }
}
