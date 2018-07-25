package org.opensingular.flow.schedule.quartz;

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
