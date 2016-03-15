/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.flow.schedule.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;

/**
 * Common base class for accessing a Quartz Scheduler, i.e. for registering jobs,
 * triggers and listeners on a {@link org.quartz.Scheduler} instance.
 * <p></p>
 * <p>For concrete usage, check out the {@link QuartzSchedulerFactory}.</p>
 *
 * @author Mirante Tecnologia
 */
public abstract class SchedulerAccessor {

    protected static final String JOB_DETAIL_KEY = "jobDetail";

    protected final Log logger = LogFactory.getLog(getClass());

    private boolean overwriteExistingJobs = false;

    private String[] jobSchedulingDataLocations;

    private List<JobDetail> jobDetails;

    private Map<String, Calendar> calendars;

    private List<Trigger> triggers;

    private SchedulerListener[] schedulerListeners;

    private JobListener[] globalJobListeners;

    private TriggerListener[] globalTriggerListeners;

    /**
     * Set whether any jobs defined on this SchedulerFactoryBean should overwrite
     * existing job definitions. Default is "false", to not overwrite already
     * registered jobs that have been read in from a persistent job store.
     */
    public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
        this.overwriteExistingJobs = overwriteExistingJobs;
    }

    /**
     * Set the location of a Quartz job definition XML file that follows the
     * "job_scheduling_data_1_5" XSD or better. Can be specified to automatically
     * register jobs that are defined in such a file, possibly in addition
     * to jobs defined directly on this SchedulerFactoryBean.
     *
     * @see org.quartz.xml.XMLSchedulingDataProcessor
     */
    public void setJobSchedulingDataLocation(String jobSchedulingDataLocation) {
        this.jobSchedulingDataLocations = new String[] {jobSchedulingDataLocation};
    }

    /**
     * Set the locations of Quartz job definition XML files that follow the
     * "job_scheduling_data_1_5" XSD or better. Can be specified to automatically
     * register jobs that are defined in such files, possibly in addition
     * to jobs defined directly on this SchedulerFactoryBean.
     *
     * @see org.quartz.xml.XMLSchedulingDataProcessor
     */
    public void setJobSchedulingDataLocations(String... jobSchedulingDataLocations) {
        this.jobSchedulingDataLocations = jobSchedulingDataLocations;
    }

    /**
     * Register a list of JobDetail objects with the Scheduler that
     * this FactoryBean creates, to be referenced by Triggers.
     * <p>This is not necessary when a Trigger determines the JobDetail
     * itself: In this case, the JobDetail will be implicitly registered
     * in combination with the Trigger.
     *
     * @see #setTriggers
     * @see org.quartz.JobDetail
     */
    public void setJobDetails(JobDetail... jobDetails) {
        this.jobDetails = new ArrayList<>(Arrays.asList(jobDetails));
    }

    /**
     * Register a list of Quartz Calendar objects with the Scheduler
     * that this FactoryBean creates, to be referenced by Triggers.
     *
     * @param calendars Map with calendar names as keys as Calendar
     * objects as values
     * @see org.quartz.Calendar
     */
    public void setCalendars(Map<String, Calendar> calendars) {
        this.calendars = calendars;
    }

    /**
     * Register a list of Trigger objects with the Scheduler that
     * this FactoryBean creates.
     * <p>If the Trigger determines the corresponding JobDetail itself,
     * the job will be automatically registered with the Scheduler.
     * Else, the respective JobDetail needs to be registered via the
     * "jobDetails" property of this FactoryBean.
     *
     * @see #setJobDetails
     * @see org.quartz.JobDetail
     */
    public void setTriggers(Trigger... triggers) {
        this.triggers = Arrays.asList(triggers);
    }

    /**
     * Specify Quartz SchedulerListeners to be registered with the Scheduler.
     */
    public void setSchedulerListeners(SchedulerListener... schedulerListeners) {
        this.schedulerListeners = schedulerListeners;
    }

    /**
     * Specify global Quartz JobListeners to be registered with the Scheduler.
     * Such JobListeners will apply to all Jobs in the Scheduler.
     */
    public void setGlobalJobListeners(JobListener... globalJobListeners) {
        this.globalJobListeners = globalJobListeners;
    }

    /**
     * Specify global Quartz TriggerListeners to be registered with the Scheduler.
     * Such TriggerListeners will apply to all Triggers in the Scheduler.
     */
    public void setGlobalTriggerListeners(TriggerListener... globalTriggerListeners) {
        this.globalTriggerListeners = globalTriggerListeners;
    }

    /**
     * Register jobs and triggers (within a transaction, if possible).
     */
    protected void registerJobsAndTriggers() throws SchedulerException {
        try {
            if (this.jobSchedulingDataLocations != null) {
                ClassLoadHelper clh = new SimpleClassLoadHelper();
                clh.initialize();
                XMLSchedulingDataProcessor dataProcessor = new XMLSchedulingDataProcessor(clh);
                for (String location : this.jobSchedulingDataLocations) {
                    dataProcessor.processFileAndScheduleJobs(location, getScheduler());
                }
            }

            if (this.jobDetails != null) {
                for (JobDetail jobDetail : this.jobDetails) {
                    addJobToScheduler(jobDetail);
                }
            } else {
                this.jobDetails = new LinkedList<>();
            }

            if (this.calendars != null) {
                for (String calendarName : this.calendars.keySet()) {
                    Calendar calendar = this.calendars.get(calendarName);
                    getScheduler().addCalendar(calendarName, calendar, true, true);
                }
            }

            if (this.triggers != null) {
                for (Trigger trigger : this.triggers) {
                    addTriggerToScheduler(trigger);
                }
            }
        } catch (Exception e) {
            if (e instanceof SchedulerException) {
                throw (SchedulerException) e;
            }
            throw new SchedulerException("Registration of jobs and triggers failed: " + e.getMessage(), e);
        }
    }

    /**
     * Add the given job to the Scheduler, if it doesn't already exist.
     * Overwrites the job in any case if "overwriteExistingJobs" is set.
     *
     * @param jobDetail the job to add
     * @return {@code true} if the job was actually added,
     * {@code false} if it already existed before
     *
     * @see #setOverwriteExistingJobs
     */
    protected boolean addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
        if (this.overwriteExistingJobs || !jobDetailExists(jobDetail)) {
            getScheduler().addJob(jobDetail, true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add the given trigger to the Scheduler, if it doesn't already exist.
     * Overwrites the trigger in any case if "overwriteExistingJobs" is set.
     *
     * @param trigger the trigger to add
     * @return {@code true} if the trigger was actually added,
     * {@code false} if it already existed before
     *
     * @see #setOverwriteExistingJobs
     */
    protected boolean addTriggerToScheduler(Trigger trigger) throws SchedulerException {
        boolean triggerExists = triggerExists(trigger);
        if (!triggerExists || this.overwriteExistingJobs) {
            // Check if the Trigger is aware of an associated JobDetail.
            JobDetail jobDetail = findJobDetail(trigger);
            if (jobDetail != null) {
                // Automatically register the JobDetail too.
                if (!this.jobDetails.contains(jobDetail) && addJobToScheduler(jobDetail)) {
                    this.jobDetails.add(jobDetail);
                }
            }
            if (!triggerExists) {
                try {
                    getScheduler().scheduleJob(trigger);
                } catch (ObjectAlreadyExistsException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Unexpectedly found existing trigger, assumably due to cluster race condition: " +
                                ex.getMessage() + " - can safely be ignored");
                    }
                    if (this.overwriteExistingJobs) {
                        rescheduleJob(trigger);
                    }
                }
            } else {
                rescheduleJob(trigger);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find the job detail of the specified trigger.
     *
     * @param trigger the trigger.
     * @return the job detail.
     */
    private JobDetail findJobDetail(Trigger trigger) {
        return (JobDetail) trigger.getJobDataMap().remove(JOB_DETAIL_KEY);
    }

    /**
     * Verify if the job detail exists.
     *
     * @param jobDetail the job detail.
     * @return {@code true} if exists; {@code false} otherwise.
     */
    private boolean jobDetailExists(JobDetail jobDetail) throws SchedulerException {
        return (getScheduler().getJobDetail(jobDetail.getKey()) != null);
    }

    /**
     * Verify if the trigger exists.
     *
     * @param trigger the trigger.
     * @return {@code true} if exists; {@code false} otherwise.
     */
    private boolean triggerExists(Trigger trigger) throws SchedulerException {
        return (getScheduler().getTrigger(trigger.getKey()) != null);
    }

    /**
     * Remove (delete) the <code>{@link org.quartz.Trigger}</code> with the
     * given key, and store the new given one - which must be associated
     * with the same job (the new trigger must have the job name & group specified)
     * - however, the new trigger need not have the same name as the old trigger.
     *
     * @param trigger The new <code>Trigger</code> to be stored.
     * @see Scheduler#rescheduleJob(TriggerKey, Trigger)
     */
    private void rescheduleJob(Trigger trigger) throws SchedulerException {
        getScheduler().rescheduleJob(trigger.getKey(), trigger);
    }

    /**
     * Register all specified listeners with the Scheduler.
     */
    protected void registerListeners() throws SchedulerException {
        ListenerManager listenerManager = getScheduler().getListenerManager();
        if (this.schedulerListeners != null) {
            for (SchedulerListener listener : this.schedulerListeners) {
                listenerManager.addSchedulerListener(listener);
            }
        }
        if (this.globalJobListeners != null) {
            for (JobListener listener : this.globalJobListeners) {
                listenerManager.addJobListener(listener, new LinkedList<>());
            }
        }
        if (this.globalTriggerListeners != null) {
            for (TriggerListener listener : this.globalTriggerListeners) {
                listenerManager.addTriggerListener(listener, new LinkedList<>());
            }
        }
    }

    /**
     * Template method that determines the Scheduler to operate on.
     * To be implemented by subclasses.
     */
    protected abstract Scheduler getScheduler();
}
