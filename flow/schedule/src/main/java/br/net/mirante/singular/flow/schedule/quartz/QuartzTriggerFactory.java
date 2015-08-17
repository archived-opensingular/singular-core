package br.net.mirante.singular.flow.schedule.quartz;

import java.util.function.Supplier;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.ScheduledJob;

public class QuartzTriggerFactory {

    private String id;
    private Supplier<Object> job;
    private IScheduleData scheduleData = null;
    private JobBuilder jobBuilder = JobBuilder.newJob().ofType(QuartzScheduledJob.class).storeDurably();
    private boolean startNow = false;

    private QuartzTriggerFactory() {
        /* CONSTRUTOR VAZIO */
    }

    public static QuartzTriggerFactory newTrigger() {
        return new QuartzTriggerFactory();
    }

    public QuartzTriggerFactory withScheduleData(IScheduleData scheduleData) {
        this.scheduleData = scheduleData;
        return this;
    }

    public QuartzTriggerFactory startNow() {
        this.startNow = true;
        return this;
    }

    public QuartzTriggerFactory withIdentity(String name, String group) {
        jobBuilder.withIdentity(name, group);
        return this;
    }

    public QuartzTriggerFactory withIdentity(String id) {
        this.id = id;
        return this;
    }

    public QuartzTriggerFactory forJob(Supplier<Object> job) {
        this.job = job;
        return this;
    }

    public Trigger build() throws SchedulerException {
        JobDetail jobDetail = configureJob(jobBuilder.build(), scheduleData);
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().forJob(jobDetail);
        if (scheduleData != null && !scheduleData.getCronExpression().isEmpty()) {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(scheduleData.getCronExpression()));
        }
        if (startNow) {
            triggerBuilder.startNow();
        }
        Trigger trigger = triggerBuilder.build();
        trigger.getJobDataMap().put(SchedulerAccessor.JOB_DETAIL_KEY, jobDetail);
        return trigger;
    }

    private JobDetail configureJob(JobDetail jobDetail, IScheduleData scheduleData) {
        jobDetail.getJobDataMap().put(QuartzJobFactory.JOB_KEY, new ScheduledJob(id, scheduleData, job));
        return jobDetail;
    }
}
