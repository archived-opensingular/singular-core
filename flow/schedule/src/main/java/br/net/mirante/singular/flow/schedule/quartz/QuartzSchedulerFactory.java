/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.net.mirante.singular.flow.schedule.quartz;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
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
public class QuartzSchedulerFactory {

    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    public static final int DEFAULT_THREAD_COUNT = 10;

    private Class<? extends SchedulerFactory> schedulerFactoryClass = StdSchedulerFactory.class;

    private String schedulerName;

    private ResourceBundle configLocation;

    private Properties quartzProperties;

    private DataSource dataSource;

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
     * @see #setBeanName
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
     * Set the default DataSource to be used by the Scheduler. If set,
     * this will override corresponding settings in Quartz properties.
     * <p>Note: If this is set, the Quartz settings should not define
     * a job store "dataSource" to avoid meaningless double configuration.
     *
     * @see #setQuartzProperties
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public void setBeanName(String name) {
        if (this.schedulerName == null) {
            this.schedulerName = name;
        }
    }

    //---------------------------------------------------------------------
    // Implementation of InitializingBean interface
    //---------------------------------------------------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.dataSource == null && this.nonTransactionalDataSource != null) {
            this.dataSource = this.nonTransactionalDataSource;
        }

        if (this.applicationContext != null && this.resourceLoader == null) {
            this.resourceLoader = this.applicationContext;
        }

        // Create SchedulerFactory instance...
        SchedulerFactory schedulerFactory = BeanUtils.instantiateClass(this.schedulerFactoryClass);
        initSchedulerFactory(schedulerFactory);

        if (this.resourceLoader != null) {
            // Make given ResourceLoader available for SchedulerFactory configuration.
            configTimeResourceLoaderHolder.set(this.resourceLoader);
        }
        if (this.taskExecutor != null) {
            // Make given TaskExecutor available for SchedulerFactory configuration.
            configTimeTaskExecutorHolder.set(this.taskExecutor);
        }
        if (this.dataSource != null) {
            // Make given DataSource available for SchedulerFactory configuration.
            configTimeDataSourceHolder.set(this.dataSource);
        }
        if (this.nonTransactionalDataSource != null) {
            // Make given non-transactional DataSource available for SchedulerFactory configuration.
            configTimeNonTransactionalDataSourceHolder.set(this.nonTransactionalDataSource);
        }

        // Get Scheduler instance from SchedulerFactory.
        try {
            this.scheduler = createScheduler(schedulerFactory, this.schedulerName);
            populateSchedulerContext();

            if (!this.jobFactorySet && !(this.scheduler instanceof RemoteScheduler)) {
                // Use AdaptableJobFactory as default for a local Scheduler, unless when
                // explicitly given a null value through the "jobFactory" bean property.
                this.jobFactory = new AdaptableJobFactory();
            }
            if (this.jobFactory != null) {
                if (this.jobFactory instanceof SchedulerContextAware) {
                    ((SchedulerContextAware) this.jobFactory).setSchedulerContext(this.scheduler.getContext());
                }
                this.scheduler.setJobFactory(this.jobFactory);
            }
        } finally {
            if (this.resourceLoader != null) {
                configTimeResourceLoaderHolder.remove();
            }
            if (this.taskExecutor != null) {
                configTimeTaskExecutorHolder.remove();
            }
            if (this.dataSource != null) {
                configTimeDataSourceHolder.remove();
            }
            if (this.nonTransactionalDataSource != null) {
                configTimeNonTransactionalDataSourceHolder.remove();
            }
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
        if (!(schedulerFactory instanceof StdSchedulerFactory)) {
            if (this.configLocation != null || this.quartzProperties != null ||
                    this.taskExecutor != null || this.dataSource != null) {
                throw new IllegalArgumentException(
                        "StdSchedulerFactory required for applying Quartz properties: " + schedulerFactory);
            }
            // Otherwise assume that no initialization is necessary...
            return;
        }

        Properties mergedProps = new Properties();

        if (this.resourceLoader != null) {
            mergedProps.setProperty(StdSchedulerFactory.PROP_SCHED_CLASS_LOAD_HELPER_CLASS,
                    ResourceLoaderClassLoadHelper.class.getName());
        }

        if (this.taskExecutor != null) {
            mergedProps.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                    LocalTaskExecutorThreadPool.class.getName());
        } else {
            // Set necessary default properties here, as Quartz will not apply
            // its default configuration when explicitly given properties.
            mergedProps.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, SimpleThreadPool.class.getName());
            mergedProps.setProperty(PROP_THREAD_COUNT, Integer.toString(DEFAULT_THREAD_COUNT));
        }

        if (this.configLocation != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Loading Quartz config from [" + this.configLocation + "]");
            }
            PropertiesLoaderUtils.fillProperties(mergedProps, this.configLocation);
        }

        CollectionUtils.mergePropertiesIntoMap(this.quartzProperties, mergedProps);

        if (this.dataSource != null) {
            mergedProps.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, LocalDataSourceJobStore.class.getName());
        }

        // Make sure to set the scheduler name as configured in the Spring configuration.
        if (this.schedulerName != null) {
            mergedProps.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, this.schedulerName);
        }

        ((StdSchedulerFactory) schedulerFactory).initialize(mergedProps);
    }

    /**
     * Create the Scheduler instance for the given factory and scheduler name.
     * Called by {@link #afterPropertiesSet}.
     * <p>The default implementation invokes SchedulerFactory's {@code getScheduler}
     * method. Can be overridden for custom Scheduler creation.
     *
     * @param schedulerFactory the factory to create the Scheduler with
     * @param schedulerName the name of the scheduler to create
     * @return the Scheduler instance
     *
     * @throws SchedulerException if thrown by Quartz methods
     * @see #afterPropertiesSet
     * @see org.quartz.SchedulerFactory#getScheduler
     */
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName)
            throws SchedulerException {

        // Override thread context ClassLoader to work around naive Quartz ClassLoadHelper loading.
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        boolean overrideClassLoader = (this.resourceLoader != null &&
                !this.resourceLoader.getClassLoader().equals(threadContextClassLoader));
        if (overrideClassLoader) {
            currentThread.setContextClassLoader(this.resourceLoader.getClassLoader());
        }
        try {
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
        } finally {
            if (overrideClassLoader) {
                // Reset original thread context ClassLoader.
                currentThread.setContextClassLoader(threadContextClassLoader);
            }
        }
    }

    /**
     * Expose the specified context attributes and/or the current
     * ApplicationContext in the Quartz SchedulerContext.
     */
    private void populateSchedulerContext() throws SchedulerException {
        // Put specified objects into Scheduler context.
        if (this.schedulerContextMap != null) {
            this.scheduler.getContext().putAll(this.schedulerContextMap);
        }

        // Register ApplicationContext in Scheduler context.
        if (this.applicationContextSchedulerContextKey != null) {
            if (this.applicationContext == null) {
                throw new IllegalStateException(
                        "SchedulerFactoryBean needs to be set up in an ApplicationContext " +
                                "to be able to handle an 'applicationContextSchedulerContextKey'");
            }
            this.scheduler.getContext().put(this.applicationContextSchedulerContextKey, this.applicationContext);
        }
    }

    /**
     * Start the Quartz Scheduler, respecting the "startupDelay" setting.
     *
     * @param scheduler the Scheduler to start
     * @param startupDelay the number of seconds to wait before starting
     * the Scheduler asynchronously
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
                    } catch (InterruptedException ex) {
                        // simply proceed
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("Starting Quartz Scheduler now, after delay of " + startupDelay + " seconds");
                    }
                    try {
                        scheduler.start();
                    } catch (SchedulerException ex) {
                        throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
                    }
                }
            };
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    //---------------------------------------------------------------------
    // Implementation of FactoryBean interface
    //---------------------------------------------------------------------

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Scheduler getObject() {
        return this.scheduler;
    }

    @Override
    public Class<? extends Scheduler> getObjectType() {
        return (this.scheduler != null) ? this.scheduler.getClass() : Scheduler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    //---------------------------------------------------------------------
    // Implementation of SmartLifecycle interface
    //---------------------------------------------------------------------

    @Override
    public void start() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                startScheduler(this.scheduler, this.startupDelay);
            } catch (SchedulerException ex) {
                throw new SchedulingException("Could not start Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public void stop() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                this.scheduler.standby();
            } catch (SchedulerException ex) {
                throw new SchedulingException("Could not stop Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public void stop(Runnable callback) throws SchedulingException {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                return !this.scheduler.isInStandbyMode();
            } catch (SchedulerException ex) {
                return false;
            }
        }
        return false;
    }

    //---------------------------------------------------------------------
    // Implementation of DisposableBean interface
    //---------------------------------------------------------------------

    /**
     * Shut down the Quartz scheduler on bean factory shutdown,
     * stopping all scheduled jobs.
     */
    @Override
    public void destroy() throws SchedulerException {
        logger.info("Shutting down Quartz Scheduler");
        this.scheduler.shutdown(this.waitForJobsToCompleteOnShutdown);
    }

}
