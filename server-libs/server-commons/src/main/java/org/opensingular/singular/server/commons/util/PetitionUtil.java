package org.opensingular.singular.server.commons.util;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.singular.server.commons.exception.PetitionWithoutDefinitionException;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;

public class PetitionUtil {

    public static <T extends PetitionEntity> ProcessDefinition<?> getProcessDefinition(T petition) {
        if (petition.getProcessDefinitionEntity() == null) {
            throw new PetitionWithoutDefinitionException();
        }
        return Flow.getProcessDefinitionWith(petition.getProcessDefinitionEntity().getKey());
    }

}