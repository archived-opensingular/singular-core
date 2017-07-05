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

import org.opensingular.flow.schedule.IScheduleData;

import javax.annotation.Nonnull;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class STaskJava extends STask<STaskJava> {

    private IScheduleData scheduleData;

    private TaskJavaCall<?> taskImpl;

    @SuppressWarnings("rawtypes")
    private TaskJavaBatchCall blockImpl;

    public STaskJava(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.JAVA;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }

    public boolean isCalledInBlock() {
        return blockImpl != null;
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    @Nonnull
    public <T extends ProcessInstance> STaskJava callBlock(@Nonnull TaskJavaBatchCall<T> batchCall,
                                                           @Nonnull IScheduleData scheduleData) {
        if (taskImpl != null) {
            throw new SingularFlowException(createErrorMsg("A task já está configurada usando call(), chamada simples"), this);
        }
        this.blockImpl = inject(batchCall);
        this.scheduleData = inject(scheduleData);
        return this;
    }

    @Nonnull
    public STaskJava call(@Nonnull TaskJavaCall<? extends ProcessInstance> javaCall) {
        if (blockImpl != null) {
            throw new SingularFlowException(createErrorMsg("A task já está configurada usando callBlock(), chamada em bloco"), this);
        }
        taskImpl = inject(javaCall);
        return this;
    }

    @Override
    public boolean isImmediateExecution() {
        return getScheduleData() == null;
    }

    @Override
    public void execute(ExecutionContext execucaoTask) {
        if (taskImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Se aplica apenas execução em bloco nesta tarefa."), this);
        }
        taskImpl.call(execucaoTask);
    }

    public Object executarByBloco(Collection<? extends ProcessInstance> instancias) {
        if (blockImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Não se aplica execução em bloco nesta tarefa."), this);
        }
        Object result = blockImpl.call(instancias);

        if (result == null) {
            long qtdAlterado = instancias.stream().filter(i -> !equals(i.getState().orElse(null))).count();
            result = "De " + instancias.size() + " instancias no estado [" + getCompleteName() + "], " + qtdAlterado + " mudaram de estado";
        }
        return result;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        if (taskImpl == null && blockImpl == null) {
            throw new SingularFlowException(createErrorMsg("Não foi configurado o código de execução da tarefa"), this);
        }
    }

}
