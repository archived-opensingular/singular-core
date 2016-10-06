package org.opensingular.singular.server.commons.schedule;

import javax.inject.Inject;

import org.springframework.transaction.PlatformTransactionManager;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;

public class TransactionalQuartzScheduledService extends QuartzScheduleService implements Loggable{

    @Inject
    private PlatformTransactionManager transactionManager;

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        super.schedule(new TransactionalScheduledJobProxy(scheduledJob, transactionManager));
        
        getLogger().info("Job("+scheduledJob+") scheduled.");
    }
}
