/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
