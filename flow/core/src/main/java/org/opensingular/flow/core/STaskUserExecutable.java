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

@SuppressWarnings("unchecked")
public abstract class STaskUserExecutable<K extends STaskUserExecutable<?>> extends STask<K> {

    private ITaskPageStrategy executionPage;
    private ITaskPageStrategy backPage;
    private ITaskPageStrategy pageAfterTask;
    private IExecutionDateStrategy<? extends ProcessInstance> targetDateExecutionStrategy;

    public STaskUserExecutable(FlowMap flowMap, String name, String abbreviation) {
        super(flowMap, name, abbreviation);
    }

    @Override
    @Nonnull
    public final K addAccessStrategy(@Nonnull TaskAccessStrategy<?> accessStrategy) {
        return (K) super.addAccessStrategy(accessStrategy);
    }

    @Override
    @Nonnull
    public K addVisualizeStrategy(@Nonnull TaskAccessStrategy<?> accessStrategy) {
        return (K) super.addVisualizeStrategy(accessStrategy);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Nonnull
    public <T extends ProcessInstance> K withTargetDate(@Nonnull IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        this.targetDateExecutionStrategy = inject(targetDateExecutionStrategy);
        return (K) this;
    }

    public final IExecutionDateStrategy<ProcessInstance> getTargetDateExecutionStrategy() {
        return (IExecutionDateStrategy<ProcessInstance>) targetDateExecutionStrategy;
    }

    public ITaskPageStrategy getBackPage() {
        return backPage;
    }

    public void setBackPage(@Nonnull ITaskPageStrategy backPage) {
        this.backPage = inject(backPage);
    }

    public ITaskPageStrategy getPageAfterTask() {
        return pageAfterTask;
    }

    public void setPageAfterTask(@Nonnull ITaskPageStrategy pageAfterTask) {
        this.pageAfterTask = inject(pageAfterTask);
    }

    public ITaskPageStrategy getExecutionPage() {
        return executionPage;
    }

    @Nonnull
    public K setExecutionPage(@Nonnull ITaskPageStrategy executionPage) {
        this.executionPage = inject(executionPage);
        return (K) this;
    }
}
