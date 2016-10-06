/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.document;

import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Recuperador de tipo com base no ID do mesmo. É provida pela aplicação host de
 * modo a permitir o recuperação do da definições tipo para o ID solicitado.
 * Tipicamente é utilziado no processo de deserialziação, recuperação de
 * instancias persistidas ou mesmo criação de uma nova versão.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class TypeLoader<TYPE_KEY extends Serializable> {

    /**
     * Retorna a referência ao tipo solicitado se possível.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    public final Optional<RefType> loadRefType(TYPE_KEY typeId) {
        return loadRefTypeImpl(Objects.requireNonNull(typeId));
    }

    /**
     * Implementa a efetiva recuperação da referência do tipo.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    protected abstract Optional<RefType> loadRefTypeImpl(TYPE_KEY typeId);

    /** Recupera o tipo solicitado se possível. */
    public final Optional<SType<?>> loadType(TYPE_KEY typeId) {
        return loadTypeImpl(typeId);
    }

    /**
     * Implementa a efetiva recuperação do tipo.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    protected abstract Optional<SType<?>> loadTypeImpl(TYPE_KEY typeId);

    /**
     * Recupera a referência ao tipo solicitado se possível ou dispara exception
     * se não encontrar.
     *
     * @exception SingularFormException
     *                Senão encontrar o tipo.
     */
    public final RefType loadRefTypeOrException(TYPE_KEY typeId) throws SingularFormException {
        return loadRefType(typeId).orElseThrow(() -> new SingularFormException("Não foi encontrado o tipo para o id=" + typeId));
    }

    /**
     * Recupera o tipo solicitado se possível ou dispara exception se não
     * encontrar.
     *
     * @exception SingularFormException
     *                Senão encontrar o tipo.
     */
    public final SType<?> loadTypeOrException(TYPE_KEY typeId) throws SingularFormException {
        return loadType(typeId).orElseThrow(() -> new SingularFormException("Não foi encontrado o tipo para o id=" + typeId));
    }
}
