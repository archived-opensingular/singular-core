/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.StartedTaskListener;
import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.TaskAccessStrategy;

import java.util.function.Consumer;

public interface BuilderTaskSelf<SELF extends BuilderTaskSelf<SELF, TASK>, TASK extends MTask<?>> extends BTask {

    @Override
    TASK getTask();

    default SELF with(Consumer<SELF> consumer) {
        consumer.accept(self());
        return self();
    }

    @SuppressWarnings("unchecked")
    default SELF self() {
        return (SELF) this;
    }

    @Override
    default SELF addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        getTask().addAccessStrategy(estrategiaAcesso);
        return self();
    }

    @Override
    default SELF addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        getTask().addVisualizeStrategy(estrategiaAcesso);
        return self();
    }

    @Override
    default SELF addStartedTaskListener(StartedTaskListener listenerInicioTarefa) {
        getTask().addStartedTaskListener(listenerInicioTarefa);
        return self();
    }
}