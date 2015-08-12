package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.function.Predicate;

public interface ITaskPredicate extends Predicate<TaskInstance>, Serializable {

    String getName();

    EventType getEventType();

    default String getFullDescription() {
        return getName();
    }

    default String getDescription(TaskInstance taskInstance) {
        return getFullDescription();
    }
}
