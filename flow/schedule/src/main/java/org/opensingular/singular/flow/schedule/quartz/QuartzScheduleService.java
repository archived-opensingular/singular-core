/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.schedule.quartz;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.quartz.JobKey;
import org.quartz.SchedulerException;

import com.google.common.base.Throwables;

import org.opensingular.singular.flow.schedule.IScheduleService;
import org.opensingular.singular.flow.schedule.IScheduledJob;

public class QuartzScheduleService implements IScheduleService {

    private static final String SCHEDULER_NAME = "SingularFlowScheduler";

    /**
     * Property file looked up in the application using this scheduler
     */
    private static final String CONFIG_RESOURCE_NAME = "quartz";

    /**
     * Property file used if the application using this scheduler does not provide
     * one. The file name is different mainly to prevent conflict with applications
     * that have this file inside a jar.
     */
    private static final String DEFAULT_CONFIG_RESOURCE_NAME = "quartz-default";

    private final QuartzSchedulerFactory quartzSchedulerFactory;

    public QuartzScheduleService() {
        this(false);
    }

    public QuartzScheduleService(QuartzSchedulerFactory quartzSchedulerFactory) {
        this.quartzSchedulerFactory = quartzSchedulerFactory;
        init();
    }

    public QuartzScheduleService(boolean waitJobsOnShutdown) {
        quartzSchedulerFactory = new QuartzSchedulerFactory();
        quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(waitJobsOnShutdown);
        init();
    }

    private void init(){
        quartzSchedulerFactory.setSchedulerName(SCHEDULER_NAME);
        ResourceBundle quartzBundle = null;
        try {
            quartzBundle = ResourceBundle.getBundle(CONFIG_RESOURCE_NAME);
        } catch (MissingResourceException mse) {
            // If this fails, we load the defaul one.
        }
        if (quartzBundle == null) {
                quartzBundle = ResourceBundle.getBundle(DEFAULT_CONFIG_RESOURCE_NAME);
        }

        quartzSchedulerFactory.setConfigLocation(quartzBundle);
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        try {
            quartzSchedulerFactory.addTrigger(
                    QuartzTriggerFactory.newTrigger()
                            .withIdentity(scheduledJob.getId())
                            .forJob(scheduledJob::run)
                            .withScheduleData(scheduledJob.getScheduleData()).build());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void trigger(IScheduledJob scheduledJob) {
        try {
            quartzSchedulerFactory.triggerJob(new JobKey(scheduledJob.getId()));
        } catch (SchedulerException e) {
            throw Throwables.propagate(e);
        }
    }
}
