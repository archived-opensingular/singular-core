/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.persistence;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe abstrata de apoio a criação de FormKey baseados em um tipo de objeto. Basta implementar o método {@link
 * #parseValuePersistenceString(String)} e que o tipo do objeto de valor tenha os métodos {@link Object#equals(Object)}
 * e {@link Object#hashCode()} implementados corretamente. <p>As classes derivadas devem ter um construtor recebendo
 * String necessariamente.</p> <p>O valor interno é imutável.</p>
 *
 * @author Daniel C. Bordin
 */
public abstract class AbstractFormKey<T extends Serializable> implements FormKey {

        @Nonnull
    private final T value;

    public AbstractFormKey(@Nonnull String persistenceString) {
        if (persistenceString == null) {
            throw new SingularFormPersistenceException("O valor da chave não pode ser null");
        }
        T newValue = parseValuePersistenceString(persistenceString);
        if (newValue == null) {
            throw new SingularFormPersistenceException(
                    "O método parsePersistenceString() retornou null para a string '" + persistenceString + "'");
        }
        value = Objects.requireNonNull(newValue);
    }

    public AbstractFormKey(@Nonnull T keyValue) {
        if (keyValue == null) {
            throw new SingularFormPersistenceException("O valor da chave não pode ser null");
        }
        value = keyValue;
    }

    @Nonnull
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
        return obj != null && obj.getClass() == getClass() && value.equals(((AbstractFormKey<?>) obj).getValue());
    }

    @Override
    public String toString() {
        return getClass().getName() + '(' + value + ')';
    }
}
