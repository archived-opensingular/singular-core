package org.opensingular.form;

/**
 * Created by ronaldtm on 15/03/17.
 */
public class SingularInvalidFieldTypeException extends SingularFormException {

    public SingularInvalidFieldTypeException(SType<?> rootType, SType<?> fieldType) {
        super(String.format(String.format(
                "O tipo '%s' não foi encontrado como subcampo de '%s'",
                fieldType.getName(),
                rootType.getName())), rootType);
        add("rootType", rootType);
        add("fieldType", fieldType);
    }
}
