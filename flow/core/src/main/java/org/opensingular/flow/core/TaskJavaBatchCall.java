package org.opensingular.flow.core;

import java.util.Collection;

/**
 * Functional interface for a batch java Task.
 * Every flow instance at the same task will be executed in one batch
 * @param <K>
 *     FlowInstance
 */
@FunctionalInterface
public interface TaskJavaBatchCall<K extends FlowInstance> {

    /**
     *
     * @param flowInstances
     *  A collection of flowInstances which the current instance is a java task {@link STaskJava} configured
     *  witht this {@link TaskJavaBatchCall} implementation
     * @return
     *  a message summarizing the execution
     */
    String call(Collection<K> flowInstances);

}
