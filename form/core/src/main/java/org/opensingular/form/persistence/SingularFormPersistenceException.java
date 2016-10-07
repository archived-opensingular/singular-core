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

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

/**
 * Representa exceptions relacioandas a camada de persistência de formulário.
 *
 * @author Daniel C. Bordin
 */
public class SingularFormPersistenceException extends SingularFormException {

    public SingularFormPersistenceException(String msg) {
        super(msg);
    }

    public SingularFormPersistenceException(String msg, SInstance instance) {
        super(msg, instance);
    }

    public SingularFormPersistenceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(Object value) {
        return (SingularFormPersistenceException) super.add(value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(String label, Object value) {
        return (SingularFormPersistenceException) super.add(label, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param level Nível de indentação da informação
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(int level, String label, Object value) {
        return (SingularFormPersistenceException) super.add(level, label, value);
    }
}
