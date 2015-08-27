package br.net.mirante.singular.flow.core;

import java.util.Objects;
import java.util.function.Supplier;

import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.IScheduledJob;
import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;

import com.google.common.base.Preconditions;

public class ProcessScheduledJob implements IScheduledJob {

    private final Class<? extends ProcessDefinition<?>> processDefinition;

    private final String name;

    private Supplier<Object> job;

    private IScheduleData scheduleData;

    @SuppressWarnings("unchecked")
    ProcessScheduledJob(ProcessDefinition<?> processDefinition, String name) {
        Objects.requireNonNull(name);
        this.processDefinition = (Class<? extends ProcessDefinition<?>>) processDefinition.getClass();
        this.name = name;
    }

    public ProcessScheduledJob call(Supplier<Object> impl) {
        this.job = impl;
        return this;
    }

    public ProcessScheduledJob call(Runnable impl) {
        return call(() -> {
            impl.run();
            return null;
        });
    }

    public Object run() {
        Objects.requireNonNull(job, "Job implementation not provided.");
        return job.get();
    }

    public ProcessScheduledJob withMonthlySchedule(int dayOfMonth, int hours, int minutes, Integer... months) {
        return withSchedule(ScheduleDataBuilder.buildMonthly(dayOfMonth, hours, minutes, months));
    }

    public ProcessScheduledJob withDailySchedule(int hora, int minuto) {
        return withSchedule(ScheduleDataBuilder.buildDaily(hora, minuto));
    }

    public ProcessScheduledJob withSchedule(IScheduleData scheduleData) {
        Preconditions.checkArgument(this.scheduleData == null, "Job already scheduled.");
        this.scheduleData = scheduleData;
        return this;
    }

    public String getId() {
        return MBPM.getDefinicao(processDefinition).getAbbreviation() + "::" + getName() + "()";
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ProcessScheduledJob [job=" + getId() + ", scheduleData=" + scheduleData + "]";
    }
}
