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

package org.opensingular.form;

import org.opensingular.form.io.ServiceRefTransientValue;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * <p>
 * Prove acesso a um serviço e ao mesmo tempo permite serializar essa referência
 * sem necessáriamente implicar na serialização de todo o serviço. É capaz de
 * recuperar a referência ao serviço depois de ser deserializado.
 * </p>
 * <p>
 * É um solução semelhante a {@link org.apache.wicket.model.IModel}.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public interface RefService<T> extends Serializable, Supplier<T> {

    /**
     * Recupera a referência do serviço desejado. Garantido a disponibilidade
     * mesmo depois de serializado e deserializado.
     */
    @Override
    public T get();

    /**
     * Retorna um novo Supplier que guarda o valor retornado do supplier
     * original entre chamadas. Dessa forma evita tentativas repetidas de
     * recuperar o serviço. Faz sentido o seu uso se houver algum custo de
     * performance em chamar get() no supplier original.
     */
    @SuppressWarnings("serial")
    public static <T> RefService<T> cached(RefService<T> supplier) {
        return new RefService<T>() {

            private transient T value;

            @Override
            public T get() {
                if (value == null) {
                    value = supplier.get();
                }
                return value;
            }
        };
    }

    /**
     * Retorna um novo Supplier que sempre retorna o valor informado.
     */
    @SuppressWarnings("serial")
    public static <T extends Serializable> RefService<T> of(T value) {
        return new RefService<T>() {
            @Override
            public T get() {
                return value;
            }
        };
    }

    /**
     * Cria uma ServiceRef para o valor informado mas que descarta o valor caso
     * a refência seja serializada. Tipicamente é utilizado para referência do
     * tipo cache ou que pode ser recalculada depois.
     */
    public static <T> ServiceRefTransientValue<T> ofToBeDescartedIfSerialized(T value) {
        return new ServiceRefTransientValue<T>(value);
    }
}
