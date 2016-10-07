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

package org.opensingular.flow.schedule;

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
