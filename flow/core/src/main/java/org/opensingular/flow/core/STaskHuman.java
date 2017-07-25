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

package org.opensingular.flow.core;

public class STaskHuman extends STaskUserExecutable<STaskHuman> {

    private boolean canReallocate = true;

    public STaskHuman(FlowMap flowMap, String name, String abbreviation) {
        super(flowMap, name, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.HUMAN;
    }

    @Override
    public boolean canReallocate() {
        return canReallocate;
    }

    public STaskHuman setCanReallocate(boolean canReallocate) {
        this.canReallocate = canReallocate;
        return this;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        if (getExecutionPage() == null) {
            throw new SingularFlowException("Não foi definida a estratégia da página para execução da tarefa.", this);
        }
        if (getAccessStrategy() == null) {
            throw new SingularFlowException("Não foi definida a estrategia de verificação de acesso da tarefa", this);
        }
    }
}
