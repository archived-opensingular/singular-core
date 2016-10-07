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

package org.opensingular.flow.schedule.quartz;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.quartz.JobKey;
import org.quartz.SchedulerException;

import com.google.common.base.Throwables;

import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.IScheduledJob;

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
