/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.singular.flow.schedule.quartz;

import org.junit.Test;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.ScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;
import org.opensingular.flow.schedule.quartz.QuartzSingularSchedulerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class QuartzScheduleServiceTest {

    private static final String JOB_ID = "jobTestID";

    private String jobRunResult;

    @Test
    public void testSchedule() throws Exception {
        assertNull(jobRunResult);


        QuartzSingularSchedulerFactory factory = new QuartzSingularSchedulerFactory();
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