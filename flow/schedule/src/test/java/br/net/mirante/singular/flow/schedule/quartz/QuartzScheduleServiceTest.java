package br.net.mirante.singular.flow.schedule.quartz;

import br.net.mirante.singular.flow.schedule.IScheduledJob;
import br.net.mirante.singular.flow.schedule.ScheduledJob;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuartzScheduleServiceTest {

    private static final String JOB_ID = "jobTestID";

    private String jobRunResult;

    @Test
    public void testSchedule() throws Exception {
        IScheduledJob job = new ScheduledJob(JOB_ID, null, () -> {
            jobRunResult = JOB_ID;
            return JOB_ID;
        });
        QuartzScheduleService quartzScheduleService = new QuartzScheduleService(true);
        quartzScheduleService.schedule(job);

        assertNull(jobRunResult);
        Thread.sleep(500);
        assertNotNull(jobRunResult);
        assertEquals(JOB_ID, jobRunResult);
    }
}