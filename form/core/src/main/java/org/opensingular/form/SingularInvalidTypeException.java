package org.opensingular.form;

/**
 * Created by ronaldtm on 15/03/17.
 */
public class SingularInvalidTypeException extends SingularFormException {

    public SingularInvalidTypeException(SInstance instance, Class<? extends SType<?>> typeClass) {
        super(String.format(String.format(
                "A instância '%s' não corresponde ao tipo '%s'",
                instance.getName(),
                typeClass.getName())),
                typeClass);
        add("instance", instance);
        add("type", typeClass);
    }

}
