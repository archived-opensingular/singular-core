package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.function.Predicate;

public abstract class TaskPredicate implements Predicate<TaskInstance>, Serializable {

    public abstract String getName();

    public String getFullDescription() {
        return getName();
    }

    public String getDescription(TaskInstance taskInstance) {
        return getFullDescription();
    }

    public EventType getEventType() {
        return EventType.Conditional;
    }
}
