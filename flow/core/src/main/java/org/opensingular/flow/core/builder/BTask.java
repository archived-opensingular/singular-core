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

import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.StartedTaskListener;
import org.opensingular.flow.core.TaskAccessStrategy;

public interface BTask {

    public MTask<?> getTask();

    public BTransition<?> go(ITaskDefinition taskRefDestiny);

    public BTransition<?> go(String actionName, ITaskDefinition taskRefDestiny);

    public BTask addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addStartedTaskListener(StartedTaskListener listenerInicioTarefa);

}