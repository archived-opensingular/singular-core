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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.STaskJava;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.flow.schedule.IScheduleData;

public interface BuilderJava<SELF extends BuilderJava<SELF>> extends BuilderTaskSelf<SELF, STaskJava> {

    default SELF call(STaskJava.ImplTaskJava impl) {
        getTask().call(impl);
        return self();
    }

    default SELF setBpmnTypeAsEmail() {
        getTask().setMetaDataValue(IFlowRenderer.SEND_EMAIL, Boolean.TRUE);
        return self();
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstanciaExecucao<T> impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> impl.executar((T) execucaoTask.getProcessInstance(), execucaoTask));
    }

    default SELF call(ImplTaskJavaReturnInstanciaTarefaExecucao impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getTaskInstance(), execucaoTask));
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstanciaExecucao<T> impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getProcessInstance(), execucaoTask);
            return null;
        });
    }

    default SELF call(ImplTaskJavaVoidInstanciaTarefaExecucao impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getTaskInstance(), execucaoTask);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstancia<T> impl) {
        return call((STaskJava.ImplTaskJava) (execucaoTask -> impl.executar((T) execucaoTask.getProcessInstance())));
    }

    default SELF call(ImplTaskJavaReturnInstanciaTarefa impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getTaskInstance()));
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstancia<T> impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getProcessInstance());
            return null;
        });
    }

    default SELF call(ImplTaskJavaVoidInstanciaTarefa impl) {
        return call((STaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getTaskInstance());
            return null;
        });
    }

    default <T extends ProcessInstance> SELF callByBlock(STaskJava.ImplTaskBlock<T> implBloco, IScheduleData scheduleData) {
        getTask().callBlock(implBloco, scheduleData);
        return self();
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaExecucao<K extends ProcessInstance> {
        void executar(K processInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaExecucao<K extends ProcessInstance> {
        Object executar(K processInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaTarefaExecucao {
        void executar(TaskInstance taskInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaTarefaExecucao {
        Object executar(TaskInstance taskInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstancia<K extends ProcessInstance> {
        void executar(K processInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstancia<K extends ProcessInstance> {
        Object executar(K processInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaTarefa {
        void executar(TaskInstance taskInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaTarefa {
        Object executar(TaskInstance taskInstance);
    }
}