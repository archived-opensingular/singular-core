package org.opensingular.singular.flow.schedule.quartz;

import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.ScheduledJob;
import org.junit.Test;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;
import org.opensingular.flow.schedule.quartz.QuartzSchedulerFactory;

import static org.junit.Assert.*;

public class QuartzScheduleServiceTest {

    private static final String JOB_ID = "jobTestID";

    private String jobRunResult;

    @Test
    public void testSchedule() throws Exception {
        assertNull(jobRunResult);


        QuartzSchedulerFactory factory = new QuartzSchedulerFactory();
        WaitForShutdownListener waiForShutdownListener = new WaitForShutdownListener(factory::getScheduler);
        factory.setSchedulerListeners(waiForShutdownListener);

        IScheduledJob job = new ScheduledJob(JOB_ID, null, () -> {
            jobRunResult = JOB_ID;
            return JOB_ID;
        });
        QuartzScheduleService quartzScheduleService = new QuartzScheduleService(factory);
        quartzScheduleService.schedule(job);

        Thread.yield();

        waiForShutdownListener.waitForShutdown();

        assertNotNull(jobRunResult);
        assertEquals(JOB_ID, jobRunResult);
    }
}