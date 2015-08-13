package br.net.mirante.singular.flow.schedule.quartz;

import java.util.function.Supplier;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.ScheduledJob;

public class QuartzScheduledJob extends ScheduledJob implements Job {

    public QuartzScheduledJob(String id, IScheduleData scheduleData, Supplier<Object> job) {
        super(id, scheduleData, job);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.run();
    }
}
