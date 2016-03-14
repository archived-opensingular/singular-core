/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

public interface ITypeContext {

    public <T extends SType<?>> T getTypeOptional(Class<T> typeClass);

    public default <T extends SType<?>> T getType(Class<T> typeClass) {
        T typeRef = getTypeOptional(typeClass);
        if (typeRef == null) {
            throw new SingularFormException("Tipo da classe '" + typeClass.getName() + "' não encontrado");
        }
        return typeRef;
    }

    public SType<?> getTypeOptional(String fullNamePath);

    public default SType<?> getType(String fullNamePath) {
        SType<?> type = getTypeOptional(fullNamePath);
        if (type == null) {
            throw new SingularFormException("Tipo '" + fullNamePath + "' não encontrado");
        }
        return type;
    }

}
