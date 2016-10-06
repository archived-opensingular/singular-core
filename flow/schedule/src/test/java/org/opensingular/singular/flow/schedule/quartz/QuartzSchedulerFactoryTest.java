package org.opensingular.singular.flow.schedule.quartz;

import org.opensingular.singular.flow.schedule.IScheduleData;
import org.opensingular.singular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.singular.flow.schedule.ScheduledJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QuartzSchedulerFactoryTest {

    private final Log LOGGER = LogFactory.getLog(QuartzSchedulerFactoryTest.class);

    private final String SCHEDULER_NAME = "SingularFlowScheduler";
    private final String SCHEDULER_INSTANCE_ID = "TEST";
    private final String SCHEDULER_JOB_STORE = "org.quartz.simpl.RAMJobStore";

    private final String JOB_GROUP = "groupTest";
    private final String JOB_NAME = "jobTest";
    private final String JOB_ID = "jobTestID";

    private QuartzSchedulerFactory quartzSchedulerFactory;
    private String jobRunResult;
    private WaitForShutdownListener waitForShutdownListener;

    @Before
    public void setUp() throws Exception {
        quartzSchedulerFactory = new QuartzSchedulerFactory();
        waitForShutdownListener = new WaitForShutdownListener(quartzSchedulerFactory::getScheduler);
        quartzSchedulerFactory.setSchedulerListeners(waitForShutdownListener);
        quartzSchedulerFactory.setSchedulerName(SCHEDULER_NAME);
        quartzSchedulerFactory.setConfigLocation(ResourceBundle.getBundle("quartz"));
        jobRunResult = null;
    }

    @After
    public void tearDown() throws Exception {
        if (quartzSchedulerFactory != null) {
            quartzSchedulerFactory.destroy();
        }
    }

    @Test
    public void testStartScheduler() throws Exception {
        try {
            quartzSchedulerFactory.initialize();
            assertFalse(quartzSchedulerFactory.isRunning());
            quartzSchedulerFactory.start();
            assertTrue(quartzSchedulerFactory.isRunning());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInitialize() throws Exception {
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();

            Scheduler scheduler = quartzSchedulerFactory.getScheduler();
            assertEquals(SCHEDULER_NAME, scheduler.getSchedulerName());
            assertEquals(SCHEDULER_INSTANCE_ID, scheduler.getMetaData().getSchedulerInstanceId());
            assertEquals(SCHEDULER_JOB_STORE, scheduler.getMetaData().getJobStoreClass().getName());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testAddJob() throws Exception {
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();

            JobDetail jobDetail = JobBuilder.newJob().ofType(QuartzScheduledJob.class)
                    .withIdentity(JOB_NAME, JOB_GROUP).storeDurably().build();
            IScheduleData scheduleData = ScheduleDataBuilder.buildDaily(0, 0);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(configureJob(jobDetail, scheduleData)).withSchedule(
                            CronScheduleBuilder.cronSchedule(scheduleData.getCronExpression()))
                    .startNow().build();
            quartzSchedulerFactory.addJob(jobDetail);
            quartzSchedulerFactory.addTrigger(trigger, jobDetail);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddTriggerAndJob() throws Exception {
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();

            JobDetail jobDetail = JobBuilder.newJob().ofType(QuartzScheduledJob.class)
                    .withIdentity(JOB_NAME, JOB_GROUP).storeDurably().build();
            IScheduleData scheduleData = ScheduleDataBuilder.buildDaily(0, 0);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(configureJob(jobDetail, scheduleData)).withSchedule(
                            CronScheduleBuilder.cronSchedule(scheduleData.getCronExpression()))
                    .startNow().build();
            quartzSchedulerFactory.addTrigger(trigger, jobDetail);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterTrigger() throws Exception {
        try {
            assertNull(jobRunResult);
            JobDetail jobDetail = JobBuilder.newJob().ofType(QuartzScheduledJob.class)
                    .withIdentity(JOB_NAME, JOB_GROUP).storeDurably().build();
            Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                    .startNow().build();
            quartzSchedulerFactory.setJobDetails(configureJob(jobDetail, null));
            quartzSchedulerFactory.setTriggers(trigger);

            quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();

            Thread.yield();
            waitForShutdownListener.waitForShutdown();

            assertNotNull(jobRunResult);
            assertEquals(JOB_ID, jobRunResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddTrigger() throws Exception {
        assertNull(jobRunResult);

        quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        quartzSchedulerFactory.initialize();
        quartzSchedulerFactory.start();
        quartzSchedulerFactory.addTrigger(
                QuartzTriggerFactory.newTrigger().withIdentity(JOB_ID)
                        .withIdentity(JOB_NAME, JOB_GROUP).forJob(() -> {
                    jobRunResult = JOB_ID;
                    return JOB_ID;
                }).startNow().build()
        );

        Thread.yield();
        waitForShutdownListener.waitForShutdown();
        assertNotNull(jobRunResult);
        assertEquals(JOB_ID, jobRunResult);
    }

    private JobDetail configureJob(JobDetail jobDetail, IScheduleData scheduleData) {
        jobDetail.getJobDataMap().put(QuartzJobFactory.JOB_KEY, new ScheduledJob(JOB_ID, scheduleData, () -> {
            jobRunResult = JOB_ID;
            return JOB_ID;
        }));
        return jobDetail;
    }
}