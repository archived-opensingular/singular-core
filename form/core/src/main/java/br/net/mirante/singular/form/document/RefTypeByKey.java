/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.document;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.SingularFormException;

import java.io.Serializable;
import java.util.Objects;

/**
 * É um recuperador de referência ao tipo baseado em um chave de identificação.
 * É necessário apenas implementar ({@link #retrieveByKey()}).
 *
 * @author Daniel C. Bordin
 */
public abstract class RefTypeByKey<KEY extends Serializable> extends RefType {

    private final KEY typeId;

    public RefTypeByKey(KEY typeId) {
        this.typeId = Objects.requireNonNull(typeId);
    }

    public RefTypeByKey(KEY typeId, SType<?> type) {
        super(type);
        this.typeId = Objects.requireNonNull(typeId);
    }

    /**
     * Implementando baseado em {@link #retrieveByKey()}.
     */
    @Override
    public final SType<?> retrieve() {
        SType<?> type = retrieveByKey(typeId);
        if (type == null) {
            throw new SingularFormException(getClass().getName() + ".retrieveByKey(KEY) retornou null");
        }
        return type;
    }

    /** Deve localizar o tipo para o id informado. Não deve retornar null. */
    public abstract SType<?> retrieveByKey(KEY typeId);

}
