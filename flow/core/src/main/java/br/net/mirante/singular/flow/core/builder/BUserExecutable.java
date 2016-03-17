/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTaskUserExecutable;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;

public interface BUserExecutable<SELF extends BUserExecutable<SELF, TASK>, TASK extends MTaskUserExecutable<?>> extends BuilderTaskSelf<SELF, TASK> {

    @Override
    public default SELF addAccessStrategy(TaskAccessStrategy<?> accessStrategy) {
        getTask().addAccessStrategy(accessStrategy);
        return self();
    }

    @Override
    public default SELF addVisualizeStrategy(TaskAccessStrategy<?> accessStrategy) {
        getTask().addVisualizeStrategy(accessStrategy);
        return self();
    }

    public default SELF withExecutionPage(ITaskPageStrategy executionPage) {
        getTask().setExecutionPage(executionPage);
        return self();
    }

    public default SELF afterTaskGoTo(ITaskPageStrategy pageAfterTask) {
        getTask().setPageAfterTask(pageAfterTask);
        return self();
    }

    public default SELF withBackPage(ITaskPageStrategy backPage) {
        getTask().setBackPage(backPage);
        return self();
    }

    public default <T extends ProcessInstance> SELF withTargetDate(IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        getTask().withTargetDate(targetDateExecutionStrategy);
        return self();
    }
}