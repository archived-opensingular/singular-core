/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.Objects;
import java.util.function.Supplier;

import org.opensingular.singular.flow.schedule.IScheduleData;
import org.opensingular.singular.flow.schedule.IScheduledJob;
import org.opensingular.singular.flow.schedule.ScheduleDataBuilder;

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
