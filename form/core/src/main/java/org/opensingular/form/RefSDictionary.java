/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import org.opensingular.form.internal.util.SerializableReference;

/**
 * Representa uma referência serializável a um dicionário. Deve ser derivado de
 * modo que ao ser deserializado seja capaz de recuperar ou recontruir o
 * dicionário. OS métodos mais comuns seria recriar o dicionário do zero ou
 * recuperar de algum cache estátivo em memória.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefSDictionary extends SerializableReference<SDictionary> {

    public RefSDictionary() {
    }

    public RefSDictionary(SDictionary dictionary) {
        super(dictionary);
    }
}
