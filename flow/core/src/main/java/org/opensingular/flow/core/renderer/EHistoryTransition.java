package org.opensingular.flow.core.renderer;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents the execution history of one specific transition.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public class EHistoryTransition {

    private final String name;

    private StatisticsTransition statistics;

    EHistoryTransition(@Nullable String name) {this.name = name;}

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public StatisticsTransition getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsTransition statistics) {
        this.statistics = statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(name, ((EHistoryTransition) o).name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
