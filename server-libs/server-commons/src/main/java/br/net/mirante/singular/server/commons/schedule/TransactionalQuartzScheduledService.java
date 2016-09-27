package br.net.mirante.singular.server.commons.schedule;

import javax.inject.Inject;

import org.springframework.transaction.PlatformTransactionManager;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.schedule.IScheduledJob;
import br.net.mirante.singular.flow.schedule.quartz.QuartzScheduleService;

public class TransactionalQuartzScheduledService extends QuartzScheduleService implements Loggable{

    @Inject
    private PlatformTransactionManager transactionManager;

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        super.schedule(new TransactionalScheduledJobProxy(scheduledJob, transactionManager));
        
        getLogger().info("Job("+scheduledJob+") scheduled.");
    }
}
