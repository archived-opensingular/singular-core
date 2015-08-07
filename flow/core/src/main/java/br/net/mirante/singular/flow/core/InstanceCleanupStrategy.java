package br.net.mirante.singular.flow.core;

import java.util.concurrent.TimeUnit;

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
}
