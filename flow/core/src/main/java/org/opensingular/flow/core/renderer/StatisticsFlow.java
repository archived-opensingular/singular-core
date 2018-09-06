package org.opensingular.flow.core.renderer;

import com.google.common.math.StatsAccumulator;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Optional;

/**
 * Represents the execution statistics of the flow as whole.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public final class StatisticsFlow {

    private final StatsAccumulator statsOnlyStarted = new StatsAccumulator();
    private final StatsAccumulator statsStartMilli = new StatsAccumulator();
    private final StatsAccumulator statsEndMilli = new StatsAccumulator();

    public void registerInstance(@Nonnull Date dtStart) {
        statsOnlyStarted.add(dtStart.getTime());
    }

    public void registerInstance(@Nonnull Date dtStart, @Nonnull Date dtEndLastEvent) {
        statsStartMilli.add(dtStart.getTime());
        statsEndMilli.add(dtEndLastEvent.getTime());
    }

    @Nonnull
    public Optional<Double> getMeanTimeExecuting() {
        return statsStartMilli.count() > 0 ? Optional.of(statsEndMilli.mean() - statsStartMilli.mean()) :
                Optional.empty();
    }

    public long getMaxEndDateEventOfSamples() {
        return (long) statsEndMilli.max();
    }

    @Nonnull
    public StatsAccumulator getStatsOnlyStarted() {
        return statsOnlyStarted;
    }

    @Nonnull
    public StatsAccumulator getStatsStartMilli() {
        return statsStartMilli;
    }

    @Nonnull
    public StatsAccumulator getStatsEndMilli() {
        return statsEndMilli;
    }
}
