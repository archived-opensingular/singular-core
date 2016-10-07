package org.opensingular.form.persistence;

/**
 * Chave baseada em long.
 *
 * @author Daniel C. Bordin
 */
public class FormKeyLong extends AbstractFormKey<Long> implements FormKeyNumber {

    public FormKeyLong(long value) {
        super(Long.valueOf(value));
    }

    public FormKeyLong(Long value) {
        super(value);
    }

    public FormKeyLong(String persistenceString) {
        super(persistenceString);
    }

    @Override
    protected Long parseValuePersistenceString(String persistenceString) {
        try {
            return Long.parseLong(persistenceString);
        } catch (Exception e) {
            throw new SingularFormPersistenceException("O valor da chave não é um long válido", e).add("key",
                    persistenceString);
        }
    }

    /**
     * Tenta converter o valor para o tipo de FormKeyLong. Se o tipo não for uma representação de chave entendível,
     * então dispara uma exception.
     *
     * @return null se o valor for null
     */
    public static FormKeyLong convertToKey(Object objectValueToBeConverted) {
        if (objectValueToBeConverted == null) {
            return null;
        } else if (objectValueToBeConverted instanceof FormKeyLong) {
            return (FormKeyLong) objectValueToBeConverted;
        } else if (objectValueToBeConverted instanceof Long) {
            return new FormKeyLong((Long) objectValueToBeConverted);
        } else if (objectValueToBeConverted instanceof Number) {
            return new FormKeyLong(((Number) objectValueToBeConverted).longValue());
        }
        throw new SingularFormPersistenceException("Não consegue converter o valor solcicitado").add("value",
                objectValueToBeConverted).add("value type", objectValueToBeConverted.getClass());
    }

    @Override
    public Long longValue() {
        return getValue();
    }

    @Override
    public Integer intValue() {
        return Integer.valueOf(getValue().intValue());
    }
}
