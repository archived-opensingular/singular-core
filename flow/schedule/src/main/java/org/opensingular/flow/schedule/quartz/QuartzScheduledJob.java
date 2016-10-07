/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.schedule.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.opensingular.flow.schedule.IScheduledJob;

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
