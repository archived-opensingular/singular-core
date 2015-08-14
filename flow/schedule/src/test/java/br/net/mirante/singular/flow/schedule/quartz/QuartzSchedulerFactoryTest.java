package br.net.mirante.singular.flow.schedule.quartz;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;

import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;

import static org.junit.Assert.*;

public class QuartzSchedulerFactoryTest {

    private static final Log LOGGER = LogFactory.getLog(QuartzSchedulerFactoryTest.class);

    private static final String SCHEDULER_NAME = "SingularFlowScheduler";
    private static final String SCHEDULER_INSTANCE_ID = "TEST";
    private static final String SCHEDULER_JOB_STORE = "org.quartz.simpl.RAMJobStore";

    private static QuartzSchedulerFactory quartzSchedulerFactory = null;

    @Before
    public void setUp() throws Exception {
        quartzSchedulerFactory = new QuartzSchedulerFactory();
        quartzSchedulerFactory.setSchedulerName(SCHEDULER_NAME);
        quartzSchedulerFactory.setConfigLocation(ResourceBundle.getBundle("quartz"));
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
    public void testAddTrigger() throws Exception {
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
            quartzSchedulerFactory.start();

            TriggerBuilder.newTrigger().forJob(JobBuilder.newJob(QuartzScheduledJob.class).build());
            quartzSchedulerFactory.addTrigger(
                    TriggerBuilder.newTrigger().forJob(JobBuilder.newJob(QuartzScheduledJob.class).build())
                            .withSchedule(CronScheduleBuilder
                                    .cronSchedule(ScheduleDataBuilder.buildDaily(0, 0).getCronExpression()))
                            .startNow()
                            .build());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}