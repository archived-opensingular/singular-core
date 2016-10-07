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

import java.util.Objects;
import java.util.function.Supplier;

import org.opensingular.flow.schedule.IScheduleData;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.ScheduleDataBuilder;

public class ProcessScheduledJob implements IScheduledJob {

    private final Class<? extends ProcessDefinition<?>> processDefinition;

    private final String name;

    private Supplier<Object> job;

    private IScheduleData scheduleData;

    @SuppressWarnings("unchecked")
    ProcessScheduledJob(ProcessDefinition<?> processDefinition, String name) {
        Objects.requireNonNull(name);
        this.processDefinition = (Class<? extends ProcessDefinition<?>>) processDefinition.getClass();
        this.name = name;
    }

    public ProcessScheduledJob call(Supplier<Object> impl) {
        this.job = impl;
        return this;
    }

    public ProcessScheduledJob call(Runnable impl) {
        return call(() -> {
            impl.run();
            return null;
        });
    }

    public Object run() {
        Objects.requireNonNull(job, "Job implementation not provided.");
        return job.get();
    }

    public ProcessScheduledJob withMonthlySchedule(int dayOfMonth, int hours, int minutes, Integer... months) {
        return withSchedule(ScheduleDataBuilder.buildMonthly(dayOfMonth, hours, minutes, months));
    }

    public ProcessScheduledJob withDailySchedule(int hora, int minuto) {
        return withSchedule(ScheduleDataBuilder.buildDaily(hora, minuto));
    }

    public ProcessScheduledJob withSchedule(IScheduleData scheduleData) {
        if(this.scheduleData != null){
            throw new SingularFlowException("Job already scheduled.");
        }
        this.scheduleData = scheduleData;
        return this;
    }

    public String getId() {
        return Flow.getProcessDefinition(processDefinition).getKey() + "::" + getName() + "()";
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ProcessScheduledJob [job=" + getId() + ", scheduleData=" + scheduleData + "]";
    }
}
