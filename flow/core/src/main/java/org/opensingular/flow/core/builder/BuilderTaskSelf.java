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

import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.StartedTaskListener;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.flow.core.property.MetaDataRef;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.function.Consumer;

public interface BuilderTaskSelf<SELF extends BuilderTaskSelf<SELF, TASK>, TASK extends STask<?>> extends BuilderTask {

    @Override
    TASK getTask();

    default SELF with(Consumer<SELF> consumer) {
        SELF self = self();
        consumer.accept(self);
        return self;
    }

    @SuppressWarnings("unchecked")
    default SELF self() {
        return (SELF) this;
    }

    @Override
    default SELF uiAccess(TaskAccessStrategy<?> accessStrategy) {
        getTask().addAccessStrategy(accessStrategy);
        return self();
    }


    @Override
    default SELF addStartedTaskListener(StartedTaskListener listenerInicioTarefa) {
        getTask().addStartedTaskListener(listenerInicioTarefa);
        return self();
    }

    @Override
    @Nonnull
    default <T extends Serializable> SELF setMetaDataValue(@Nonnull MetaDataRef<T> propRef, T value) {
        getTask().setMetaDataValue(propRef, value);
        return self();
    }
}