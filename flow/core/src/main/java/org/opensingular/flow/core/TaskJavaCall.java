package org.opensingular.flow.core;

import java.io.Serializable;

/**
 * Interface funcional representando a chamada java a ser executada em uma determinada
 * taskjava do Singular Flow
 * @param <K>
 */
@FunctionalInterface
public interface TaskJavaCall<K extends FlowInstance> extends Serializable {
    void call(ExecutionContext<K> execucaoTask);
}
