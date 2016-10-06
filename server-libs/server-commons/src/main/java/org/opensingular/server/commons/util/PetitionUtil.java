package org.opensingular.server.commons.util;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.server.commons.exception.PetitionWithoutDefinitionException;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;

public class PetitionUtil {

    public static <T extends PetitionEntity> ProcessDefinition<?> getProcessDefinition(T petition) {
        if (petition.getProcessDefinitionEntity() == null) {
            throw new PetitionWithoutDefinitionException();
        }
        return Flow.getProcessDefinitionWith(petition.getProcessDefinitionEntity().getKey());
    }

}