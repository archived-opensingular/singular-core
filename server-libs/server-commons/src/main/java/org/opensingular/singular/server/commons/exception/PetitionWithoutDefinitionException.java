package org.opensingular.singular.server.commons.exception;

public class PetitionWithoutDefinitionException extends SingularServerException {

    public PetitionWithoutDefinitionException() {
        super("Nenhuma definição está vinculada a petição");
    }

}