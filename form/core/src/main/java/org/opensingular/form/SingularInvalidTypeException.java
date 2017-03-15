package org.opensingular.form;

/**
 * Created by ronaldtm on 15/03/17.
 */
public class SingularInvalidTypeException extends SingularFormException {

    private final SInstance instance;
    private final Class<? extends SType<?>> typeClass;

    public SingularInvalidTypeException(SInstance instance, Class<? extends SType<?>> typeClass) {
        super(String.format(String.format(
                "A instância '%s' não corresponde ao tipo '%s'",
                instance.getName(),
                typeClass.getName())),
                typeClass);
        this.instance = instance;
        this.typeClass = typeClass;
        add("instance", instance);
        add("type", typeClass);
    }

    public SInstance getInstance() { return instance; }
    public Class<? extends SType<?>> getTypeClass() {
        return this.typeClass;
    }
}
