/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.schedule.quartz;

import org.opensingular.flow.schedule.IScheduledJob;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzJobFactory implements JobFactory {

    public static final String JOB_KEY = IScheduledJob.class.getName();

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        IScheduledJob job = (IScheduledJob) triggerFiredBundle.getJobDetail().getJobDataMap().get(JOB_KEY);
        return new QuartzScheduledJob(job);
    }
}
