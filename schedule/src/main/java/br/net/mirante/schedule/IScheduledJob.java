package br.net.mirante.schedule;

public interface IScheduledJob {

    IScheduleData getScheduleData();

    Object run();

    String getId();
}
