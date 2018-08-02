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

package org.opensingular.schedule.quartz;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.schedule.IScheduleService;
import org.opensingular.schedule.IScheduledJob;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

public class QuartzScheduleService implements IScheduleService, Loggable {

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

    private SingularQuartzSchedulerAcessor quartzSchedulerFactory;

    /**
     * This constructor could be use to set startImmediate false, for waiting something before start the schedule factory.
     * <p>Note: In case of use Spring dataSource configuration, this should be used with value false.
     *
     * @param startImmediate True for immediate start.
     */
    public QuartzScheduleService(boolean startImmediate) {
        this(false, startImmediate);
    }

    /**
     * Default constructor.
     * Default: Start immediate, and don't wait jobs on shutdown.
     */
    public QuartzScheduleService() {
        this(false, true);
    }

    public QuartzScheduleService(SingularQuartzSchedulerAcessor quartzSchedulerFactory) {
        this.quartzSchedulerFactory = quartzSchedulerFactory;
        initialize();
    }

    public QuartzScheduleService(boolean waitJobsOnShutdown, boolean startImmediate) {
        quartzSchedulerFactory = new QuartzSingularSchedulerFactory();
        quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(waitJobsOnShutdown);
        if (startImmediate) {
            initialize();
        }
    }

    private void initialize() {
        configureQuartzProperties();
        init();
    }

    /**
     * Method for use configuration of the <code>quartz.properties</code>
     * If don't have it will use the defauls <code>quartz-default.properties</code>
     */
    private void configureQuartzProperties() {
        ResourceBundle quartzBundle = null;
        try {
            quartzBundle = ResourceBundle.getBundle(CONFIG_RESOURCE_NAME);
        } catch (MissingResourceException mse) {
            getLogger().debug(null, mse);
            // If this fails, we load the defaul one.
        }
        if (quartzBundle == null) {
            quartzBundle = ResourceBundle.getBundle(DEFAULT_CONFIG_RESOURCE_NAME);
        }

        quartzSchedulerFactory.setConfigLocation(quartzBundle);
    }

    /**
     * Method for initialize the scheduler.
     * <p>
     * Method responsible for add all the trigger's.
     */
    protected void init() {
        quartzSchedulerFactory.setSchedulerName(SCHEDULER_NAME);
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();
        } catch (Exception e) {
            throw SingularException.rethrow(e);
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
            throw SingularException.rethrow(e);
        }
    }

    @Override
    public void trigger(IScheduledJob scheduledJob) {
        try {
            quartzSchedulerFactory.triggerJob(new JobKey(scheduledJob.getId()));
        } catch (SchedulerException e) {
            throw SingularException.rethrow(e);
        }
    }

    @Override
    public void shutdown() {
        try {
            quartzSchedulerFactory.destroy();
        } catch (SchedulerException e) {
            getLogger().trace(e.getMessage(), e);
        }
    }

    @Override
    public Set<JobKey> getAllJobKeys() throws SchedulerException {
        return quartzSchedulerFactory.getAllJobKeys();
    }

    public void setQuartzSchedulerFactory(SingularQuartzSchedulerAcessor quartzSchedulerFactory) {
        this.quartzSchedulerFactory = quartzSchedulerFactory;
    }
}
