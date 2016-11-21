package org.opensingular.server.commons.persistence.dto;

import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public class PetitionHistoryDTO implements Serializable {

    private TaskInstanceEntity           task;
    private PetitionContentHistoryEntity petitionContentHistory;

    public TaskInstanceEntity getTask() {
        return task;
    }

    public PetitionHistoryDTO setTask(TaskInstanceEntity task) {
        this.task = task;
        return this;
    }

    public PetitionContentHistoryEntity getPetitionContentHistory() {
        return petitionContentHistory;
    }

    public PetitionHistoryDTO setPetitionContentHistory(PetitionContentHistoryEntity petitionContentHistory) {
        this.petitionContentHistory = petitionContentHistory;
        return this;
    }


    public String getTaskName() {
        return task.getTask().getName();
    }

    public Date getBeginDate() {
        return Optional
                .ofNullable(petitionContentHistory)
                .map(PetitionContentHistoryEntity::getHistoryDate)
                .orElse(
                        Optional
                                .ofNullable(task.getBeginDate())
                                .orElse(null));
    }


    public String getAllocatedUser() {
        return Optional
                .ofNullable(petitionContentHistory)
                .map(PetitionContentHistoryEntity::getActor)
                .map(Actor::getNome)
                .orElse(null);
    }

    public Date getEndDate() {
        return Optional
                .ofNullable(task.getEndDate())
                .orElse(null);
    }
}