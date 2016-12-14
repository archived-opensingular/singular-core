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

package org.opensingular.flow.core.defaults;

import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskAccessStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NullTaskAccessStrategy extends TaskAccessStrategy<ProcessInstance> {

    @Override
    public boolean canExecute(ProcessInstance instance, MUser user) {
        return true;
    }

    @Override
    public Set<Integer> getFirstLevelUsersCodWithAccess(ProcessInstance instancia) {
        return Collections.emptySet();
    }

    @Override
    public List<? extends MUser> listAllocableUsers(ProcessInstance instancia) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        return Collections.emptyList();
    }
}
