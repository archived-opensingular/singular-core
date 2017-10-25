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

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Providers information associated to a particular role of a flow.
 */
@SuppressWarnings("serial")
public abstract class BusinessRoleStrategy<K extends FlowInstance> implements Serializable {

    /**
     * List all users that may be attributed to the role for a specific flow instance. One scenery is to use this list
     * in a user interface to choose a person assigned to role form the listed users.
     */
    @Nonnull
    public abstract List<? extends SUser> listAllowedUsers(@Nonnull K instance);

    /**
     * Return the user tha should be automatically assigned to role in the case of the execution of specific flow
     * instance e taskInstance.
     */
    @Nonnull
    public Optional<SUser> getUserForRole(@Nonnull K instance, @Nonnull TaskInstance task) {
        return instance.getLastFinishedTask().map(lastTask -> lastTask.getResponsibleUser());
    }
}
