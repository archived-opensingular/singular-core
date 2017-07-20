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

import java.util.Date;
import java.util.function.BiFunction;

/**
 * Functional interface responsible for calculating how long the wait task should wait.
 * It takes as parameters the current {@link FlowInstance} and the {@link TaskInstance} and should return the target
 * expiration date.
 * This Strategy IS CALLED ONLY ONCE and the expiration date is persisted.
 * From time to time (not fixed) the {@link FlowEngine} will check the current server time against the expiration date
 * and if it is before the expiration date the {@link FlowEngine} will try to move forward through
 * the default transition (throw exception and possibly stuck the workflow if there is no default transaction)
 * @param <K>
 *      generic parameter bounded to FlowInstance
 */
@FunctionalInterface
public interface IExecutionDateStrategy<K extends FlowInstance> extends BiFunction<K, TaskInstance, Date> {

}