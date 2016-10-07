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
