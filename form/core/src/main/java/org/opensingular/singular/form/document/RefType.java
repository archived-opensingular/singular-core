/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.document;

import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.internal.util.SerializableReference;

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
    public <T extends SType<?>> RefType createSubReference(Class<T> typeClass) {
        return new RefType() {
            @Override
            protected SType<?> retrieve() {
                return RefType.this.get().getDictionary().getType(typeClass);
            }
        };
    }
}
