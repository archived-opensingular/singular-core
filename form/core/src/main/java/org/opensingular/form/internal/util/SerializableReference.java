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

package org.opensingular.form.internal.util;

import org.opensingular.form.SingularFormException;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * <p>
 * É uma classe que pode ser serializada com segurança e que mantém uma
 * referência a um objeto que não será serializado, mas que será recuperado
 * quando necessário mediate a chamada do método {@link #retrieve()}
 * <p>
 * <p>
 * Pode ser criada com conteúdo null. Nesse caso, irá chamar {@link #retrieve()}
 * na primeira chamada a {@link #get()}.
 * </p>
 *
 * @author Daniel
 *
 * @param <K>
 */
public abstract class SerializableReference<K> implements Serializable, Supplier<K> {

    private transient K reference;

    public SerializableReference() {
    }

    public SerializableReference(K reference) {
        this.reference = reference;
    }

    @Override
    public final K get() {
        if (reference == null) {
            reference = retrieve();
            if (reference == null) {
                throw new SingularFormException(getClass().getName() + ".retrieve() retornou null");
            }
        }
        return reference;
    }

    /**
     * Altera o objeto referenciado, se o mesmo ainda estiver null. Caso
     * contrário dispara exception.
     */
    public final void set(K reference) {
        if (this.reference != null) {
            throw new SingularFormException(getClass().getName() + ": Referencia já definida. Não pode ser trocada.");
        }
        this.reference = reference;
    }

    /**
     * Método chamado para recupera a instância depois de uma deserialização ou
     * caso a referência tenha sido inicializada vazia.
     *
     * @return Não pode retornar null.
     */
    protected abstract K retrieve();

}
