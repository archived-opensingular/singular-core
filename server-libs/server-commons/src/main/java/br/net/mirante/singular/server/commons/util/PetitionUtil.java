package br.net.mirante.singular.server.commons.util;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.server.commons.exception.PetitionWithoutDefinitionException;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;

public class PetitionUtil {

    public static <T extends PetitionEntity> ProcessDefinition<?> getProcessDefinition(T petition) {
        if (petition.getProcessDefinitionEntity() == null) {
            throw new PetitionWithoutDefinitionException();
        }
        return Flow.getProcessDefinitionWith(petition.getProcessDefinitionEntity().getKey());
    }

}