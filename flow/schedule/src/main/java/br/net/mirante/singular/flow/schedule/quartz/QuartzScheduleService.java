package br.net.mirante.singular.flow.schedule.quartz;

import java.util.ResourceBundle;

import org.quartz.Scheduler;
import org.quartz.Trigger;

import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.schedule.IScheduledJob;

import com.google.common.base.Throwables;

public class QuartzScheduleService implements IScheduleService {

    private final QuartzSchedulerFactory quartzSchedulerFactory;

    public QuartzScheduleService() {
        quartzSchedulerFactory = new QuartzSchedulerFactory();
        quartzSchedulerFactory.setSchedulerName("SingularFlowScheduler");
        quartzSchedulerFactory.setConfigLocation(ResourceBundle.getBundle("quartz"));
        try {
            quartzSchedulerFactory.initialize();
            quartzSchedulerFactory.start();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        try {
            Trigger trigger = QuartzTriggerFactory.newTrigger()
                .withIdentity(scheduledJob.getId())
                .forJob(scheduledJob::run)
                .withScheduleData(scheduledJob.getScheduleData()).build();

            getScheduler().scheduleJob(trigger);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Scheduler getScheduler() {
        return quartzSchedulerFactory.getScheduler();
    }
}
