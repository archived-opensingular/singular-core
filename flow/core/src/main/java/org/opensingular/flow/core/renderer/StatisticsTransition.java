package org.opensingular.flow.core.renderer;

import com.google.common.math.StatsAccumulator;

import javax.annotation.Nonnull;

/**
 * Represents the execution statistics of a particular transition of the flow.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public final class StatisticsTransition {

    private final StatsAccumulator statsDuration = new StatsAccumulator();

    @Nonnull
    public StatsAccumulator getStatsDuration() {
        return statsDuration;
    }

    public int count() {
        return (int) statsDuration.count();
    }
}
