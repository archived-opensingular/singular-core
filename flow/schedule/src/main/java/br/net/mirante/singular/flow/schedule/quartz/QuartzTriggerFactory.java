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

/**
 * Classe para criação de triggers e jobs do Quartz usando as interfaces definidas.
 */
public class QuartzTriggerFactory {

    /**
     * O identificador do job.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#getId()
     */
    private String id;
    /**
     * O {@link Supplier} do job a ser criado.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#run()
     */
    private Supplier<Object> job;
    /**
     * Os dados do job.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#getScheduleData()
     */
    private IScheduleData scheduleData = null;
    /**
     * O {@link JobBuilder} desta fábrica. Este é definido do tipo {@link QuartzScheduledJob}.
     */
    private JobBuilder jobBuilder = JobBuilder.newJob().ofType(QuartzScheduledJob.class).storeDurably();
    /**
     * O indicador de disparo imediato.
     */
    private boolean startNow = false;

    /**
     * Instancia uma nova fábrica de triggers.
     */
    private QuartzTriggerFactory() {
        /* CONSTRUTOR VAZIO */
    }

    /**
     * Inicia a criação de um novo trigger.
     *
     * @return uma nova instância desta fábrica.
     */
    public static QuartzTriggerFactory newTrigger() {
        return new QuartzTriggerFactory();
    }

    /**
     * Informa os dados do job a ser criado para o trigger.
     *
     * @param scheduleData os dados.
     * @return está fábrica.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#getScheduleData()
     */
    public QuartzTriggerFactory withScheduleData(IScheduleData scheduleData) {
        this.scheduleData = scheduleData;
        return this;
    }

    /**
     * Define que a trigger terá um disparo imediato.
     *
     * @return está fábrica.
     */
    public QuartzTriggerFactory startNow() {
        this.startNow = true;
        return this;
    }

    /**
     * Define o nome e o grupo do job a ser criado para o trigger.
     *
     * @param name o nome do job.
     * @param group o grupo do job.
     * @return está fábrica.
     */
    public QuartzTriggerFactory withIdentity(String name, String group) {
        jobBuilder.withIdentity(name, group);
        return this;
    }

    /**
     * Define o identificador do job a ser criado para o trigger.
     *
     * @param id o(a) id.
     * @return está fábrica.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#getId()
     */
    public QuartzTriggerFactory withIdentity(String id) {
        this.id = id;
        return this;
    }

    /**
     * Define o {@link Supplier} do job a ser criado para o trigger.
     *
     * @param job o {@link Supplier} do job.
     * @return está fábrica.
     * @see br.net.mirante.singular.flow.schedule.IScheduledJob#run()
     */
    public QuartzTriggerFactory forJob(Supplier<Object> job) {
        this.job = job;
        return this;
    }

    /**
     * Cria o trigger com as definições fornecidas.
     *
     * @return o trigger criado.
     * @throws SchedulerException caso ocorra algum erro do tipo {@link SchedulerException}.
     */
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

    /**
     * Configura o job com as informações fornecidas.
     *
     * @param jobDetail os detalhes do job.
     * @param scheduleData os dados do job.
     * @return os detalhes do job com as informações extras adicionadas.
     */
    private JobDetail configureJob(JobDetail jobDetail, IScheduleData scheduleData) {
        jobDetail.getJobDataMap().put(QuartzJobFactory.JOB_KEY, new ScheduledJob(id, scheduleData, job));
        return jobDetail;
    }
}
