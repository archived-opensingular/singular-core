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
