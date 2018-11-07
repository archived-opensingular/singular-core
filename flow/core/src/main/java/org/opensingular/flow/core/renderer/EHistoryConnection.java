package org.opensingular.flow.core.renderer;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represent all transitions from a source node to a destination node. Usually there is only one transition from a
 * source to a destination, but in some cases, may be two transitions with different names.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public class EHistoryConnection {

    private final String taskOrigin;
    private final String taskDestination;

    private Map<String, EHistoryTransition> paths;

    EHistoryConnection(@Nonnull String taskOrigin, @Nonnull String taskDestination) {
        this.taskOrigin = taskOrigin;
        this.taskDestination = taskDestination;
    }

    @Nonnull
    public String getTaskOrigin() {
        return taskOrigin;
    }

    @Nonnull
    public String getTaskDestination() {
        return taskDestination;
    }

    public int size() {
        return paths == null ? 0 : paths.size();
    }

    @Nonnull
    public Collection<EHistoryTransition> getTransitions() {
        return paths == null ? Collections.emptyList() : paths.values();
    }

    @Nonnull
    EHistoryTransition addTransition(@Nullable String transitionName) {
        if (paths == null) {
            paths = new HashMap<>();
        }
        String name = transitionName == null ? "*" : transitionName;
        return paths.computeIfAbsent(name, EHistoryTransition::new);
    }

    @Nonnull
    public Optional<EHistoryTransition> getTransition(@Nullable String transitionName) {
        if (paths != null) {
            if (transitionName != null) {
                EHistoryTransition history = paths.get(transitionName);
                if (history != null) {
                    return Optional.of(history);
                }
            }
            return Optional.ofNullable(paths.get("*"));
        }
        return Optional.empty();
    }

    public boolean contains(@Nullable String transitionName) {
        return getTransition(transitionName).isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EHistoryConnection that = (EHistoryConnection) o;
        return Objects.equals(taskOrigin, that.taskOrigin) && Objects.equals(taskDestination, that.taskDestination);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(taskOrigin).append(taskDestination).toHashCode();
    }

    @Override
    public String toString() {
        return "[" + getTransitions().stream().map(EHistoryTransition::getName).collect(Collectors.joining(", ")) + "]";
    }
}
