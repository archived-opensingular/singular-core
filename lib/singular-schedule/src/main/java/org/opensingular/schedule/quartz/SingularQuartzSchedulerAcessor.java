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

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.opensingular.lib.commons.base.SingularException;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;

/**
 * Interface responsible for the integration of Quartz Scheduler and Singular.
 */
public interface SingularQuartzSchedulerAcessor {
    /**
     * The PROP_THREAD_COUNT constant.
     */
    String SINGULAR_PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";
    /**
     * The DEFAULT_THREAD_COUNT constant.
     */
    int SINGULAR_DEFAULT_THREAD_COUNT = 10;

    void setSchedulerName(String schedulerName);

    void setConfigLocation(ResourceBundle configLocation);

    void setQuartzProperties(Properties quartzProperties);

    void setJobFactory(JobFactory jobFactory);

    void setExposeSchedulerInRepository(boolean exposeSchedulerInRepository);

    void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown);

    void initialize() throws SingularException;

    Scheduler getScheduler();

    void start() throws SchedulerException;

    void start(int startupDelay) throws SchedulerException;

    void stop() throws SchedulerException;

    void stop(Runnable callback) throws SchedulerException;

    boolean isRunning();

    void destroy() throws SchedulerException;

    void addJob(JobDetail jobDetail) throws SchedulerException;

    void addTrigger(Trigger trigger, JobDetail jobDetail) throws SchedulerException;

    void addTrigger(Trigger trigger) throws SchedulerException;

    void triggerJob(JobKey jobKey) throws SchedulerException;

    Set<JobKey> getAllJobKeys() throws SchedulerException;
}
