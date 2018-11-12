package org.opensingular.flow.core.renderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents the execution history of one task.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public class EHistoryTask {

    private final String name;

    private StatisticsTask statistics;

    EHistoryTask(@Nonnull String name) {this.name = name;}

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public StatisticsTask getStatistics() {return statistics;}

    public void setStatistics(StatisticsTask statistics) {
        this.statistics = statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(name, ((EHistoryTask) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
