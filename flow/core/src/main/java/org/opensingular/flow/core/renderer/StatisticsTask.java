package org.opensingular.flow.core.renderer;

import com.google.common.math.StatsAccumulator;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represents the execution statistics of a particular task of the flow.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public final class StatisticsTask {

    private final StatsAccumulator statsDuration = new StatsAccumulator();
    private final StatsAccumulator statsLast = new StatsAccumulator();
    private int countStarts = 0;
    private int countIns = 0;
    private int countOuts = 0;

    @Nonnull
    public StatsAccumulator getStatsDuration() {
        return statsDuration;
    }

    @Nonnull
    public StatsAccumulator getStatsLast() {
        return statsLast;
    }

    public int getCountStarts() {
        return countStarts;
    }

    public int getCountIns() {
        return countIns;
    }

    public int getCountOuts() {
        return countOuts;
    }

    public int countCurrents() {
        return countIns - countOuts;
    }

    public void registerStart() {
        countStarts++;
        countIns++;
    }

    public void registerOutTransition(long durationMilli) {
        statsDuration.add(durationMilli);
        countOuts++;
    }

    public void registerInTransition() {
        countIns++;
    }

    public void registerLast(long dtStartLastMilli) {
        statsLast.add(dtStartLastMilli);
    }

    @Nonnull
    public Optional<Double> getMeanDateStartAsLast() {
        return statsLast.count() > 0 ? Optional.of(statsLast.mean()) : Optional.empty();
    }
}
