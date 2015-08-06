package br.net.mirante.schedule.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import br.net.mirante.schedule.ScheduledJob;

/**
 * Classe encapsuladora de <i>jobs</i> do Quartz que implementam a interface {@link Runnable}.
 */
@DisallowConcurrentExecution
public class GenericQuartzJob<T extends ScheduledJob> extends QuartzJobBean {

    private String jobName;
    private Object lastResult;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");
            @SuppressWarnings("unchecked")
            T job = (T) applicationContext.getBean(jobName);
            lastResult = job.run();
        } catch (Exception ex) {
            throw new JobExecutionException("Fail to execute job: " + jobName, ex);
        }
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Object getLastResult() {
        return lastResult;
    }

    public void setLastResult(Object lastResult) {
        this.lastResult = lastResult;
    }
}
