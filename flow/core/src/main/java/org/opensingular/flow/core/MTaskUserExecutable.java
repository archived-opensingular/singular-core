/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;


@SuppressWarnings("unchecked")
public abstract class MTaskUserExecutable<K extends MTaskUserExecutable<?>> extends MTask<K> {

    private ITaskPageStrategy executionPage;
    private ITaskPageStrategy backPage;
    private ITaskPageStrategy pageAfterTask;
    private IExecutionDateStrategy<? extends ProcessInstance> targetDateExecutionStrategy;

    public MTaskUserExecutable(FlowMap flowMap, String name, String abbreviation) {
        super(flowMap, name, abbreviation);
    }

    @Override
    public final K addAccessStrategy(TaskAccessStrategy<?> accessStrategy) {
        return (K) super.addAccessStrategy(accessStrategy);
    }

    @Override
    public K addVisualizeStrategy(TaskAccessStrategy<?> accessStrategy) {
        return (K) super.addVisualizeStrategy(accessStrategy);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    public <T extends ProcessInstance> K withTargetDate(IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        this.targetDateExecutionStrategy = targetDateExecutionStrategy;
        return (K) this;
    }

    public final IExecutionDateStrategy<ProcessInstance> getTargetDateExecutionStrategy() {
        return (IExecutionDateStrategy<ProcessInstance>) targetDateExecutionStrategy;
    }

    public ITaskPageStrategy getBackPage() {
        return backPage;
    }

    public void setBackPage(ITaskPageStrategy backPage) {
        this.backPage = backPage;
    }

    public ITaskPageStrategy getPageAfterTask() {
        return pageAfterTask;
    }

    public void setPageAfterTask(ITaskPageStrategy pageAfterTask) {
        this.pageAfterTask = pageAfterTask;
    }

    public ITaskPageStrategy getExecutionPage() {
        return executionPage;
    }

    public K setExecutionPage(ITaskPageStrategy executionPage) {
        this.executionPage = executionPage;
        return (K) this;
    }

}
