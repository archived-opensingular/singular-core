package org.opensingular.form;

/**
 * Created by ronaldtm on 15/03/17.
 */
public class SingularInvalidFieldTypeException extends SingularFormException {

    private final SType<?> rootType, fieldType;

    public SingularInvalidFieldTypeException(SType<?> rootType, SType<?> fieldType) {
        super(String.format(String.format(
                "O tipo '%s' não foi encontrado como subcampo de '%s'",
                fieldType.getName(),
                rootType.getName())), rootType);
        this.rootType = rootType;
        this.fieldType = fieldType;
        add("rootType", rootType);
        add("fieldType", fieldType);
    }

    public SType<?> getRootType() {
        return this.rootType;
    }

    public SType<?> getFieldType() {
        return fieldType;
    }
}
