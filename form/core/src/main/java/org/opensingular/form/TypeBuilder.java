/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

/**
 * Classe de suporte a construção de um tipo durante a chamada do método {@link SType#onLoadType(TypeBuilder)}.
 */
public class TypeBuilder {
    //TODO (por Daniel Bordin 29/05/2016) Por em quanto não é muito útil essa classe. Verificar a permanência dela se
    // não encontrarmos utilidade até o fim do ano

    private final SType<?> targetType;

    <X extends SType<?>> TypeBuilder(X newType) {
        this.targetType = newType;
    }

    final SType<?> getType() {
        return targetType;
    }
}
