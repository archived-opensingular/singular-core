package org.opensingular.singular.form.persistence;

/**
 * Chave baseada em int.
 *
 * @author Daniel C. Bordin
 */
public class FormKeyInt extends AbstractFormKey<Integer> implements FormKeyNumber {

    public FormKeyInt(int value) {
        super(Integer.valueOf(value));
    }

    public FormKeyInt(Integer value) {
        super(value);
    }

    public FormKeyInt(String persistenceString) {
        super(persistenceString);
    }

    @Override
    protected Integer parseValuePersistenceString(String persistenceString) {
        try {
            return Integer.parseInt(persistenceString);
        } catch (Exception e) {
            throw new SingularFormPersistenceException("O valor da chave não é um inteiro válido", e).add("key",
                    persistenceString);
        }
    }

    public static FormKeyInt convertToKey(Object objectValueToBeConverted) {
        if (objectValueToBeConverted == null) {
            throw new SingularFormPersistenceException("Não pode converter um valor null para FormKey");
        } else if (objectValueToBeConverted instanceof FormKeyInt) {
            return (FormKeyInt) objectValueToBeConverted;
        } else if (objectValueToBeConverted instanceof Integer) {
            return new FormKeyInt((Integer) objectValueToBeConverted);
        } else if (objectValueToBeConverted instanceof Number) {
            return new FormKeyInt(((Number) objectValueToBeConverted).intValue());
        }
        throw new SingularFormPersistenceException("Não consegue converter o valor solcicitado").add("value",
                objectValueToBeConverted).add("value type", objectValueToBeConverted.getClass());
    }

    @Override
    public Long longValue() {
        return Long.valueOf(getValue());
    }

    @Override
    public Integer intValue() {
        return getValue();
    }
}
