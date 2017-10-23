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
import javax.annotation.Nullable;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class STaskJava extends STask<STaskJava> {

    private IScheduleData scheduleData;

    private TaskJavaCall<?> taskImpl;

    @SuppressWarnings("rawtypes")
    private TaskJavaBatchCall blockImpl;

    private DisplayType displayType;

    public STaskJava(FlowMap flowMap, String name, String abbreviation) {
        super(flowMap, name, abbreviation);
    }

    @Override
    public TaskType getTaskType() {
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
    public <T extends FlowInstance> STaskJava batchCall(@Nonnull TaskJavaBatchCall<T> batchCall,
                                                           @Nonnull IScheduleData scheduleData) {
        if (taskImpl != null) {
            throw new SingularFlowException(createErrorMsg("A task já está configurada usando call(), chamada simples"), this);
        }
        this.blockImpl = inject(batchCall);
        this.scheduleData = inject(scheduleData);
        return this;
    }

    @Nonnull
    public STaskJava call(@Nonnull TaskJavaCall<? extends FlowInstance> javaCall) {
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
    public void execute(ExecutionContext executionContext) {
        if (taskImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Se aplica apenas execução em bloco nesta tarefa."), this);
        }
        taskImpl.call(executionContext);
    }

    public Object executarByBloco(Collection<? extends FlowInstance> instances) {
        if (blockImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Não se aplica execução em bloco nesta tarefa."), this);
        }
        String result = blockImpl.call(instances);

        if (result == null) {
            long qtdAlterado = instances.stream().filter(i -> !equals(i.getState().orElse(null))).count();
            result = "De " + instances.size() + " instancias no estado [" + getCompleteName() + "], " + qtdAlterado + " mudaram de estado";
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

    /**
     * Defines, for the purpose of generating a diagram of the flow, the type of BPMN node that will be used to
     * render this task.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    public void setDisplayType(@Nullable DisplayType displayType) {
        this.displayType = displayType;
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the type of BPMN node that will be used to
     * render this task.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public DisplayType getDisplayType() {
        return displayType;
    }
}
