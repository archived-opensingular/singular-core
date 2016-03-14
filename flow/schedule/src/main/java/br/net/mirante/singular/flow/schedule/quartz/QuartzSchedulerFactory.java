/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.flow.schedule.quartz;

import java.io.IOException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.RemoteScheduler;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobFactory;

/**
 * Factory that creates and configures a Quartz {@link org.quartz.Scheduler}.
 * <p></p>
 * <p>Allows registration of JobDetails, Calendars and Triggers, automatically
 * starting the scheduler on initialization and shutting it down on destruction.
 * In scenarios that just require static registration of jobs at startup, there
 * is no need to access the Scheduler instance itself in application code.</p>
 * <p></p>
 * <p>For dynamic registration of jobs at runtime, use a bean reference to
 * this SchedulerFactoryBean to get direct access to the Quartz Scheduler
 * ({@code org.quartz.Scheduler}). This allows you to create new jobs
 * and triggers, and also to control and monitor the entire Scheduler.</p>
 * <p></p>
 * <p>Note that Quartz instantiates a new Job for each execution, in
 * contrast to Timer which uses a TimerTask instance that is shared
 * between repeated executions. Just JobDetail descriptors are shared.</p>
 *
 * @author Mirante Tecnologia
 * @see org.quartz.Scheduler
 * @see org.quartz.SchedulerFactory
 * @see org.quartz.impl.StdSchedulerFactory
 */
public class QuartzSchedulerFactory extends SchedulerAccessor {

    /**
     * The PROP_THREAD_COUNT constant.
     */
    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    /**
     * The DEFAULT_THREAD_COUNT constant.
     */
    public static final int DEFAULT_THREAD_COUNT = 10;

    private Class<? extends SchedulerFactory> schedulerFactoryClass = StdSchedulerFactory.class;

    private String schedulerName;

    private ResourceBundle configLocation;

    private Properties quartzProperties;

    private JobFactory jobFactory;

    private boolean jobFactorySet = false;

    private boolean exposeSchedulerInRepository = false;

    private boolean waitForJobsToCompleteOnShutdown = false;

    private Scheduler scheduler;

    /**
     * Set the Quartz SchedulerFactory implementation to use.
     * <p>Default is {@link StdSchedulerFactory}, reading in the standard
     * {@code quartz.properties} from {@code quartz.jar}.
     * To use custom Quartz properties, specify the "configLocation"
     * or "quartzProperties" bean property on this FactoryBean.
     *
     * @see org.quartz.impl.StdSchedulerFactory
     * @see #setConfigLocation
     * @see #setQuartzProperties
     */
    public void setSchedulerFactoryClass(Class<? extends SchedulerFactory> schedulerFactoryClass) {
        this.schedulerFactoryClass = schedulerFactoryClass;
    }

    /**
     * Set the name of the Scheduler to create via the SchedulerFactory.
     * <p>If not specified, the bean name will be used as default scheduler name.
     *
     * @see org.quartz.SchedulerFactory#getScheduler()
     * @see org.quartz.SchedulerFactory#getScheduler(String)
     */
    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    /**
     * Set the location of the Quartz properties config file, for example
     * as classpath resource "classpath:quartz.properties".
     * <p>Note: Can be omitted when all necessary properties are specified
     * locally via this bean, or when relying on Quartz' default configuration.
     *
     * @see #setQuartzProperties
     */
    public void setConfigLocation(ResourceBundle configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set Quartz properties, like "org.quartz.threadPool.class".
     * <p>Can be used to override values in a Quartz properties config file,
     * or to specify all necessary properties locally.
     *
     * @see #setConfigLocation
     */
    public void setQuartzProperties(Properties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    /**
     * Set the Quartz JobFactory to use for this Scheduler.
     * <p>Default is {@link QuartzJobFactory}, which supports
     * {@link br.net.mirante.singular.flow.schedule.IScheduledJob} objects as well as standard Quartz
     * {@link org.quartz.Job} instances. Note that this default only applies
     * to a <i>local</i> Scheduler, not to a RemoteScheduler (where setting
     * a custom JobFactory is not supported by Quartz).
     *
     * @see QuartzJobFactory
     */
    public void setJobFactory(JobFactory jobFactory) {
        this.jobFactory = jobFactory;
        this.jobFactorySet = true;
    }

    /**
     * Set whether to expose the {@link Scheduler} instance in the
     * Quartz {@link SchedulerRepository}. Default is "false", since the
     * Scheduler is usually exclusively intended for access within the context.
     * <p>Switch this flag to "true" in order to expose the Scheduler globally.
     * This is not recommended unless you have an existing application that
     * relies on this behavior.
     */
    public void setExposeSchedulerInRepository(boolean exposeSchedulerInRepository) {
        this.exposeSchedulerInRepository = exposeSchedulerInRepository;
    }

    /**
     * Set whether to wait for running jobs to complete on shutdown.
     * <p>Default is "false". Switch this to "true" if you prefer
     * fully completed jobs at the expense of a longer shutdown phase.
     *
     * @see org.quartz.Scheduler#shutdown(boolean)
     */
    public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    /**
     * This method allows the instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     * as failure to set an essential property) or if initialization fails.
     */
    public void initialize() throws Exception {
        SchedulerFactory schedulerFactory = this.schedulerFactoryClass.newInstance();
        initSchedulerFactory(schedulerFactory);

        this.scheduler = createScheduler(schedulerFactory, this.schedulerName);

        if (!this.jobFactorySet && !(this.scheduler instanceof RemoteScheduler)) {
            /* Use QuartzJobFactory as default for a local Scheduler, unless when
             * explicitly given a null value through the "jobFactory" property.
             */
            this.jobFactory = new QuartzJobFactory();
        }
        if (this.jobFactory != null) {
            this.scheduler.setJobFactory(this.jobFactory);
        }

        registerListeners();
        registerJobsAndTriggers();
    }

    /**
     * Load and/or apply Quartz properties to the given SchedulerFactory.
     *
     * @param schedulerFactory the SchedulerFactory to initialize
     */
    private void initSchedulerFactory(SchedulerFactory schedulerFactory) throws SchedulerException, IOException {
        Properties mergedProps = new Properties();

        mergedProps.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, SimpleThreadPool.class.getName());
        mergedProps.setProperty(PROP_THREAD_COUNT, Integer.toString(DEFAULT_THREAD_COUNT));

        if (this.configLocation != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Loading Quartz config from [" + this.configLocation + "]");
            }
            this.fillProperties(mergedProps, this.configLocation);
        }

        this.mergePropertiesIntoMap(this.quartzProperties, mergedProps);

        // Make sure to set the scheduler name as configured in the Spring configuration.
        if (this.schedulerName != null) {
            mergedProps.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, this.schedulerName);
        }

        ((StdSchedulerFactory) schedulerFactory).initialize(mergedProps);
    }

    /**
     * Fill the given properties from the given resource (in ISO-8859-1 encoding).
     *
     * @param mergedProps the Properties instance to fill
     * @param configLocation the resource to load from
     */
    private void fillProperties(Properties mergedProps, ResourceBundle configLocation) {
        if (mergedProps == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (configLocation != null) {
            for (Enumeration<?> en = configLocation.getKeys(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value;
                try {
                    value = configLocation.getString(key);
                } catch (MissingResourceException e) {
                    logger.info(e.getMessage(), e);
                    value = null;
                }
                assert value != null;
                mergedProps.put(key, value);
            }
        }
    }

    /**
     * Merge the given Properties instance into the given Map,
     * copying all properties (key-value pairs) over.
     * <p>Uses {@code Properties.propertyNames()} to even catch
     * default properties linked into the original Properties instance.
     *
     * @param quartzProperties the Properties instance to merge (may be {@code null})
     * @param mergedProps the target Map to merge the properties into
     */
    private void mergePropertiesIntoMap(Properties quartzProperties, Properties mergedProps) {
        if (mergedProps == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (quartzProperties != null) {
            for (Enumeration<?> en = quartzProperties.propertyNames(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value = quartzProperties.getProperty(key);
                if (value == null) {
                    value = quartzProperties.get(key);
                }
                assert value != null;
                mergedProps.put(key, value);
            }
        }
    }

    /**
     * Create the Scheduler instance for the given factory and scheduler name.
     * Called by {@link #initialize()}.
     * <p>The default implementation invokes SchedulerFactory's {@code getScheduler}
     * method. Can be overridden for custom Scheduler creation.
     *
     * @param schedulerFactory the factory to create the Scheduler with
     * @param schedulerName the name of the scheduler to create
     * @return the Scheduler instance
     *
     * @throws SchedulerException if thrown by Quartz methods
     * @see #initialize()
     * @see org.quartz.SchedulerFactory#getScheduler
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName)
            throws SchedulerException {
        SchedulerRepository repository = SchedulerRepository.getInstance();
        synchronized (repository) {
            Scheduler existingScheduler = (schedulerName != null ? repository.lookup(schedulerName) : null);
            Scheduler newScheduler = schedulerFactory.getScheduler();
            if (newScheduler == existingScheduler) {
                throw new IllegalStateException("Active Scheduler of name '" + schedulerName + "' already registered " +
                        "in Quartz SchedulerRepository. Cannot create a new Spring-managed Scheduler of the same name!");
            }
            if (!this.exposeSchedulerInRepository) {
                // Need to remove it in this case, since Quartz shares the Scheduler instance by default!
                SchedulerRepository.getInstance().remove(newScheduler.getSchedulerName());
            }
            return newScheduler;
        }
    }

    /**
     * Start the Quartz Scheduler, respecting the "startupDelay" setting.
     *
     * @param scheduler the Scheduler to start
     * @param startupDelay the number of seconds to wait before starting
     * the Scheduler asynchronously
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
        if (startupDelay <= 0) {
            logger.info("Starting Quartz Scheduler now");
            scheduler.start();
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Will start Quartz Scheduler [" + scheduler.getSchedulerName() +
                        "] in " + startupDelay + " seconds");
            }
            Thread schedulerThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(startupDelay * 1000);
                    } catch (InterruptedException e) {
                        logger.info(e.getMessage(), e);
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("Starting Quartz Scheduler now, after delay of " + startupDelay + " seconds");
                    }
                    try {
                        scheduler.start();
                    } catch (SchedulerException e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            };
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    /**
     * Method that determines the Scheduler to operate on.
     */
    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Start the scheduler immediately.
     *
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void start() throws SchedulerException {
        start(0);
    }


    /**
     * Start the scheduler, respecting the "startupDelay" setting.
     *
     * @param startupDelay the number of seconds to wait before starting
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void start(int startupDelay) throws SchedulerException {
        if (this.scheduler != null) {
            startScheduler(this.scheduler, startupDelay);
        }
    }

    /**
     * Temporarily halts the <code>Scheduler</code>'s firing of <code>{@link Trigger}s</code>.
     *
     * <p>
     * When <code>start()</code> is called (to bring the scheduler out of
     * stand-by mode), trigger misfire instructions will NOT be applied
     * during the execution of the <code>start()</code> method - any misfires
     * will be detected immediately afterward (by the <code>JobStore</code>'s
     * normal process).
     * </p>
     *
     * <p>
     * The scheduler is not destroyed, and can be re-started at any time.
     * </p>
     *
     * @see #start()
     */
    public void stop() throws SchedulerException {
        if (this.scheduler != null) {
            this.scheduler.standby();
        }
    }

    /**
     * Call {@code callback.run()} after {@link #stop()}.
     *
     * @param callback the callback.
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void stop(Runnable callback) throws SchedulerException {
        stop();
        callback.run();
    }

    /**
     * Reports whether the <code>Scheduler</code> is in stand-by mode.
     *
     * @see #stop()
     * @see #start()
     */
    public boolean isRunning() {
        if (this.scheduler != null) {
            try {
                return !this.scheduler.isInStandbyMode();
            } catch (SchedulerException e) {
                logger.info(e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    /**
     * Shut down the Quartz scheduler on factory shutdown,
     * stopping all scheduled jobs.
     */
    public void destroy() throws SchedulerException {
        logger.info("Shutting down Quartz Scheduler");
        this.scheduler.shutdown(this.waitForJobsToCompleteOnShutdown);
    }

    /**
     * Add a job.
     *
     * @param jobDetail the job detail.
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void addJob(JobDetail jobDetail) throws SchedulerException {
        addJobToScheduler(jobDetail);
    }

    /**
     * Add a trigger with the specified job detail.
     *
     * @param trigger the trigger.
     * @param jobDetail the job detail.
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void addTrigger(Trigger trigger, JobDetail jobDetail) throws SchedulerException {
        trigger.getJobDataMap().put(JOB_DETAIL_KEY, jobDetail);
        addTriggerToScheduler(trigger);
    }

    /**
     * Add trigger and the trigger's job detail.
     *
     * @param trigger the trigger.
     * @throws SchedulerException if could not start Quartz Scheduler.
     */
    public void addTrigger(Trigger trigger) throws SchedulerException {
        addJobToScheduler((JobDetail) trigger.getJobDataMap().get(JOB_DETAIL_KEY));
        addTriggerToScheduler(trigger);
    }
}
