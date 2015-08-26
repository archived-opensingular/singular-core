package br.net.mirante.singular.flow.schedule.quartz;

import java.util.ResourceBundle;

import com.google.common.base.Throwables;

import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.schedule.IScheduledJob;

public class QuartzScheduleService implements IScheduleService {

    private static final String SCHEDULER_NAME = "SingularFlowScheduler";
    private static final String CONFIG_RESOURCE_NAME = "quartz";

    private final QuartzSchedulerFactory quartzSchedulerFactory;

    public QuartzScheduleService() {
        this(false);
    }

    public QuartzScheduleService(boolean waitJobsOnShutdown) {
        quartzSchedulerFactory = new QuartzSchedulerFactory();
        quartzSchedulerFactory.setSchedulerName(SCHEDULER_NAME);
        quartzSchedulerFactory.setConfigLocation(ResourceBundle.getBundle(CONFIG_RESOURCE_NAME));
        quartzSchedulerFactory.setWaitForJobsToCompleteOnShutdown(waitJobsOnShutdown);
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
            quartzSchedulerFactory.addTrigger(
                    QuartzTriggerFactory.newTrigger()
                            .withIdentity(scheduledJob.getId())
                            .forJob(scheduledJob::run)
                            .withScheduleData(scheduledJob.getScheduleData()).build());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
