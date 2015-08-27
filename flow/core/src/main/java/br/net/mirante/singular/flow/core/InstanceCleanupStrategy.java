package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

/**
 * @deprecated não deve ficar no core, específico do alocpro
 */
@Deprecated
//TODO refatorar remover
class InstanceCleanupStrategy {

    private final FlowMap flowMap;
    private final TimeUnit timeUnit;
    private final int time;

    InstanceCleanupStrategy(FlowMap map, int time, TimeUnit timeUnit) {
        this.flowMap = map;
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getTime() {
        return time;
    }

    public FlowMap getFlowMap() {
        return flowMap;
    }

    /**
     * @return {@link Date} now minus {@link #getTime()} according to {@link #getTimeUnit()}
     */
    public Date toDate() {
        return DateTime.now().minus(timeUnit.toMillis(time)).toDate();
    }
}
