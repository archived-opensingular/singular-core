package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.flow.schedule.IScheduledJob;
import br.net.mirante.singular.flow.schedule.quartz.QuartzScheduleService;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionalQuartzScheduledService extends QuartzScheduleService {

    private final PlatformTransactionManager transactionManager;

    public TransactionalQuartzScheduledService(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        super.schedule(new TransactionalScheduledJobProxy(scheduledJob, transactionManager));
    }
}
