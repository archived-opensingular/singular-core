package br.net.mirante.singular.flow.schedule.quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzJobFactory implements JobFactory {
    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        try {
            return triggerFiredBundle.getJobDetail().getJobClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SchedulerException(e);
        }
    }
}
