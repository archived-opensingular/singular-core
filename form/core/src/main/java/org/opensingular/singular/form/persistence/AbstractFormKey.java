package org.opensingular.singular.form.persistence;

/**
 * Classe abstrata de apoio a criação de FormKey baseados em um tipo de objeto. Basta implementar o método {@link
 * #parseValuePersistenceString(String)} e que o tipo do objeto de valor tenha os métodos {@link Object#equals(Object)}
 * e {@link Object#hashCode()} implementados corretamente. <p>As classes derivadas devem ter um construtor recebendo
 * String necessariamente.</p> <p>O valor interno é imutável.</p>
 *
 * @author Daniel C. Bordin
 */
public abstract class AbstractFormKey<T> implements FormKey {

    private final T value;

    public AbstractFormKey(String persistenceString) {
        if (persistenceString == null) {
            throw new SingularFormPersistenceException("O valor da chave não pode ser null");
        }
        T newValue = parseValuePersistenceString(persistenceString);
        if (newValue == null) {
            throw new SingularFormPersistenceException(
                    "O método parsePersistenceString() retornou null para a string '" + persistenceString + "'");
        }
        this.value = newValue;
    }

    public AbstractFormKey(T value) {
        if (value == null) {
            throw new SingularFormPersistenceException("O valor da chave não pode ser null");
        }
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * Método que deve ser implementado para converter uma string de persistência do valor da chave de volta no tipo
     * interno da chave. Não pode retornar null.
     */
    protected abstract T parseValuePersistenceString(String persistenceString);

    @Override
    public String toStringPersistence() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            return value.equals(((AbstractFormKey<T>) obj).getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName() + '(' + value + ')';
    }
}
