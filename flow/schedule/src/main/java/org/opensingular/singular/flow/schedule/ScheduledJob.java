/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.schedule;

import java.util.function.Supplier;

import com.google.common.base.Preconditions;

public class ScheduledJob implements IScheduledJob {

    private final String id;

    private final Supplier<Object> job;

    private final IScheduleData scheduleData;

    public ScheduledJob(String id, IScheduleData scheduleData, Supplier<Object> job) {
        super();
        this.id = id;
        this.job = job;
        this.scheduleData = scheduleData;
    }

    public Object run() {
        Preconditions.checkNotNull(job, "Job implementation not provided.");
        return job.get();
    }

    public String getId() {
        return id;
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    @Override
    public String toString() {
        return "ScheduledJob [job=" + getId() + ", scheduleData=" + scheduleData + "]";
    }
}
