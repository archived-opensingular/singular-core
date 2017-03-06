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

package org.opensingular.form.document;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.internal.util.SerializableReference;
import org.opensingular.lib.commons.lambda.ISupplier;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * É uma referência serializável a um tipo, o que permite o uso em contexto que
 * necessitam serialização/deserialização do mesmo, tipicamente durante a
 * edição. O método {@link #retrieve()} deve ser implementado de forma que
 * quando deserializado a referência, o mesmo seja capaz de localizar (ou
 * recriar) o tipo novamente.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefType extends SerializableReference<SType<?>> {

    public RefType() {
    }

    public RefType(SType<?> type) {
        super(type);
    }

    /**
     * Cria uma nova refência que utilizará o mesmo dicionário do RefType atual
     * para localizar o tipo informado.
     */
    @Nonnull
    public <T extends SType<?>> RefType createSubReference(Class<T> typeClass) {
        return new RefType() {
            @Override
            @Nonnull
            protected SType<?> retrieve() {
                return RefType.this.get().getDictionary().getType(typeClass);
            }
        };
    }

    /**
     * Produz uma referencia a partir de um provedor serializável mais simples.
     * @param supplier Provedor do tipo, o qual nunca deva gerar um valor null
     */
    @Nonnull
    public static RefType of(@Nonnull ISupplier<SType<?>> supplier) {
        Objects.requireNonNull(supplier);
        return new RefType() {
            @Override
            @Nonnull
            protected SType<?> retrieve() {
                SType<?> type = supplier.get();
                if (type == null) {
                    throw new SingularFormException(supplier.getClass().getName() + ".get() retornou null");
                }
                return type;
            }
        };
    }

    /**
     * Cria uma referência para a classe informada, que simplesmente criar um novo dicionário e um tipo a partir da
     * classe informada sempre que necessário recriar o tipo.
     */
    @Nonnull
    public static RefType of(@Nonnull Class<? extends SType> typeClass) {
        Objects.requireNonNull(typeClass);
        return new RefType() {
            @Override
            @Nonnull
            protected SType<?> retrieve() {
                return SDictionary.create().getType(typeClass);
            }
        };
    }
}
