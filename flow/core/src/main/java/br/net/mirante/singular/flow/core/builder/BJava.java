package br.net.mirante.singular.flow.core.builder;

import java.io.Serializable;

import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;

public interface BJava<SELF extends BJava<SELF>> extends BuilderTaskSelf<SELF, MTaskJava> {

    public default SELF call(MTaskJava.ImplTaskJava impl) {
        getTask().call(impl);
        return self();
    }

    @SuppressWarnings("unchecked")
    public default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstanciaExecucao<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar((T) execucaoTask.getInstanciaProcesso(), execucaoTask));
    }

    public default SELF call(ImplTaskJavaReturnInstanciaTarefaExecucao impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getInstanciaTarefa(), execucaoTask));
    }

    @SuppressWarnings("unchecked")
    public default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstanciaExecucao<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getInstanciaProcesso(), execucaoTask);
            return null;
        });
    }

    public default SELF call(ImplTaskJavaVoidInstanciaTarefaExecucao impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getInstanciaTarefa(), execucaoTask);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public default <T extends ProcessInstance> SELF call(ImplTaskJavaReturnInstancia<T> impl) {
        return call((MTaskJava.ImplTaskJava) (execucaoTask -> impl.executar((T) execucaoTask.getInstanciaProcesso())));
    }

    public default SELF call(ImplTaskJavaReturnInstanciaTarefa impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> impl.executar(execucaoTask.getInstanciaTarefa()));
    }

    @SuppressWarnings("unchecked")
    public default <T extends ProcessInstance> SELF call(ImplTaskJavaVoidInstancia<T> impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar((T) execucaoTask.getInstanciaProcesso());
            return null;
        });
    }

    public default SELF call(ImplTaskJavaVoidInstanciaTarefa impl) {
        return call((MTaskJava.ImplTaskJava) execucaoTask -> {
            impl.executar(execucaoTask.getInstanciaTarefa());
            return null;
        });
    }

    @FunctionalInterface
    public interface ImplTaskJavaVoidInstanciaExecucao<K extends ProcessInstance> extends Serializable {
        public void executar(K processInstance, ExecucaoMTask execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskJavaReturnInstanciaExecucao<K extends ProcessInstance> extends Serializable {
        public Object executar(K processInstance, ExecucaoMTask execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskJavaVoidInstanciaTarefaExecucao extends Serializable {
        public void executar(TaskInstance taskInstance, ExecucaoMTask execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskJavaReturnInstanciaTarefaExecucao extends Serializable {
        public Object executar(TaskInstance taskInstance, ExecucaoMTask execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskJavaVoidInstancia<K extends ProcessInstance> extends Serializable {
        public void executar(K processInstance);
    }

    @FunctionalInterface
    public interface ImplTaskJavaReturnInstancia<K extends ProcessInstance> extends Serializable {
        public Object executar(K processInstance);
    }

    @FunctionalInterface
    public interface ImplTaskJavaVoidInstanciaTarefa extends Serializable {
        public void executar(TaskInstance taskInstance);
    }

    @FunctionalInterface
    public interface ImplTaskJavaReturnInstanciaTarefa extends Serializable {
        public Object executar(TaskInstance taskInstance);
    }
}