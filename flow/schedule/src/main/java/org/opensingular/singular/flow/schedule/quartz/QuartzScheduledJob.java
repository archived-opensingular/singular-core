/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.schedule.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.opensingular.singular.flow.schedule.IScheduledJob;

public class QuartzScheduledJob implements Job {

    private IScheduledJob job;
    private Object lastJobRunResult;

    public QuartzScheduledJob(IScheduledJob job) {
        this.job = job;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        lastJobRunResult = this.job.run();
    }

    public Object getLastJobRunResult() {
        return lastJobRunResult;
    }
}
