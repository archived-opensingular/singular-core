package org.opensingular.server.commons.persistence.dto;

import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;

import java.io.Serializable;

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
}