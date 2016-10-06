/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.builder;

import org.opensingular.singular.flow.core.ExecutionContext;
import org.opensingular.singular.flow.core.MTaskJava;
import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.flow.core.TaskInstance;
import org.opensingular.singular.flow.schedule.IScheduleData;

import java.io.Serializable;

public interface BJava<SELF extends BJava<SELF>> extends BuilderTaskSelf<SELF, MTaskJava> {

    default SELF call(MTaskJava.ImplTaskJava impl) {
        getTask().call(impl);
        return self();
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstanciaExecucao<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar((T) execucaoTask.getProcessInstance(), execucaoTask));
    }

    default SELF call(ImplTaskJavaReturnInstanciaTarefaExecucao impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getTaskInstance(), execucaoTask));
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstanciaExecucao<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getProcessInstance(), execucaoTask);
            return null;
        });
    }

    default SELF call(ImplTaskJavaVoidInstanciaTarefaExecucao impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getTaskInstance(), execucaoTask);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstancia<T> impl) {
        return call((MTaskJava.ImplTaskJava) (execucaoTask -> impl.executar((T) execucaoTask.getProcessInstance())));
    }

    default SELF call(ImplTaskJavaReturnInstanciaTarefa impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getTaskInstance()));
    }

    @SuppressWarnings("unchecked")
    default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstancia<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getProcessInstance());
            return null;
        });
    }

    default SELF call(ImplTaskJavaVoidInstanciaTarefa impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getTaskInstance());
            return null;
        });
    }

    default <T extends ProcessInstance> SELF callByBlock(MTaskJava.ImplTaskBlock<T> implBloco, IScheduleData scheduleData) {
        getTask().callBlock(implBloco, scheduleData);
        return self();
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaExecucao<K extends ProcessInstance> extends Serializable {
        void executar(K processInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaExecucao<K extends ProcessInstance> extends Serializable {
        Object executar(K processInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaTarefaExecucao extends Serializable {
        void executar(TaskInstance taskInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaTarefaExecucao extends Serializable {
        Object executar(TaskInstance taskInstance, ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstancia<K extends ProcessInstance> extends Serializable {
        void executar(K processInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstancia<K extends ProcessInstance> extends Serializable {
        Object executar(K processInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaVoidInstanciaTarefa extends Serializable {
        void executar(TaskInstance taskInstance);
    }

    @FunctionalInterface
    interface ImplTaskJavaReturnInstanciaTarefa extends Serializable {
        Object executar(TaskInstance taskInstance);
    }
}