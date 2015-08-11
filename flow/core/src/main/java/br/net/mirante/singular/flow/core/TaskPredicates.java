package br.net.mirante.singular.flow.core;


import java.util.Date;

import org.joda.time.DateTime;

public class TaskPredicates {

    public static TaskPredicate disabledCreator() {
        return new TaskPredicate() {
            @Override
            public boolean test(TaskInstance taskInstance) {
                MUser p = taskInstance.getProcessInstance().getPessoaCriadora();
                return MBPM.canBeAllocated(p);
            }

            @Override
            public String getName() {
                return "Criador Demanda Inativado";
            }
        };
    }

    public static TaskPredicate timeLimitInDays(final int numberOfDays) {
        return new TaskPredicate() {
            @Override
            public boolean test(TaskInstance taskInstance) {
                Date date = taskInstance.getDataAlvoFim();
                if (date != null) {
                    Date limit = new DateTime(date).plusDays(numberOfDays).toDate();
                    return limit.before(new Date());
                }
                return false;
            }

            @Override
            public String getName() {
                return "Prazo Extrapolado";
            }

            @Override
            public String getFullDescription() {
                return getName() + " em " + numberOfDays + " dias";
            }

            @Override
            public EventType getEventType() {
                return EventType.Timer;
            }
        };
    }
}
