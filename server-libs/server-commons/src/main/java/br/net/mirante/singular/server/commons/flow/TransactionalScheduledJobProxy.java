package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.IScheduledJob;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


public class TransactionalScheduledJobProxy implements IScheduledJob {

    private final IScheduledJob              job;
    private final PlatformTransactionManager transactionManager;

    public TransactionalScheduledJobProxy(final IScheduledJob job, final PlatformTransactionManager transactionManager) {
        this.job = job;
        this.transactionManager = transactionManager;
    }

    @Override
    public IScheduleData getScheduleData() {
        return job.getScheduleData();
    }

    @Override
    public Object run() {
        return new TransactionTemplate(transactionManager).execute(status -> job.run());
    }

    @Override
    public String getId() {
        return job.getId();
    }

}