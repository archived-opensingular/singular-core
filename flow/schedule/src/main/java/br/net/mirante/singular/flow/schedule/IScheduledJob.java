package br.net.mirante.singular.flow.schedule;

public interface IScheduledJob {

    IScheduleData getScheduleData();

    Object run();

    String getId();
}
