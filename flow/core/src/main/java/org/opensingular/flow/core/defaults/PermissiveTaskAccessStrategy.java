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

import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.TaskAccessStrategy;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Permissive task access strategy that do not restrict any functionality
 * Not that this access strategy do not interfere with the {@link org.opensingular.flow.core.UITransitionAccessStrategy}
 * regarding the transitions permissions.
 */
public class PermissiveTaskAccessStrategy extends TaskAccessStrategy<FlowInstance> {

    @Override
    public boolean canExecute(FlowInstance instance, SUser user) {
        return true;
    }

    @Override
    public Set<Integer> getFirstLevelUsersCodWithAccess(FlowInstance instance) {
        return Collections.emptySet();
    }

    @Override
    @Nonnull
    public List<? extends SUser> listAllowedUsers(@Nonnull FlowInstance instance) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getExecuteRoleNames(FlowDefinition<?> definition, STask<?> task) {
        return Collections.emptyList();
    }
}
