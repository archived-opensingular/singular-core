package br.net.mirante.singular.flow.schedule.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.net.mirante.singular.flow.schedule.IScheduledJob;

public class QuartzScheduledJob implements Job {

    private IScheduledJob job;
    private Object lastJobRunResult;

    public QuartzScheduledJob(IScheduledJob job) {
        this.job = job;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        lastJobRunResult = this.job.run();
    }

    public Object getLastJobRunResult() {
        return lastJobRunResult;
    }
}
