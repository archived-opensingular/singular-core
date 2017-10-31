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

import org.opensingular.flow.schedule.IScheduleData;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.ScheduleDataBuilder;

import java.util.Objects;
import java.util.function.Supplier;

public class FlowScheduledJob implements IScheduledJob {

    private final Class<? extends FlowDefinition<?>> flowDefinition;

    private final String name;

    private Supplier<Object> job;

    private IScheduleData scheduleData;

    @SuppressWarnings("unchecked")
    FlowScheduledJob(FlowDefinition<?> flowDefinition, String name) {
        Objects.requireNonNull(name);
        this.flowDefinition = (Class<? extends FlowDefinition<?>>) flowDefinition.getClass();
        this.name = name;
    }

    public FlowScheduledJob call(Supplier<Object> impl) {
        this.job = impl;
        return this;
    }

    public FlowScheduledJob call(Runnable impl) {
        return call(() -> {
            impl.run();
            return null;
        });
    }

    public Object run() {
        Objects.requireNonNull(job, "Job implementation not provided.");
        return job.get();
    }

    public FlowScheduledJob withMonthlySchedule(int dayOfMonth, int hours, int minutes, Integer... months) {
        return withSchedule(ScheduleDataBuilder.buildMonthly(dayOfMonth, hours, minutes, months));
    }

    public FlowScheduledJob withDailySchedule(int hour, int minute) {
        return withSchedule(ScheduleDataBuilder.buildDaily(hour, minute));
    }

    public FlowScheduledJob withSchedule(IScheduleData scheduleData) {
        if(this.scheduleData != null){
            throw new SingularFlowException("Job already scheduled.");
        }
        this.scheduleData = scheduleData;
        return this;
    }

    public String getId() {
        return Flow.getFlowDefinition(flowDefinition).getKey() + "::" + getName() + "()";
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FlowScheduledJob [job=" + getId() + ", scheduleData=" + scheduleData + "]";
    }
}
