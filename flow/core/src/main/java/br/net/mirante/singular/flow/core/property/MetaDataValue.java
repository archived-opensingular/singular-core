/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.property;

import java.io.Serializable;

/**
 * Representa um par propriedade e seu valor.
 *
 * @author Daniel C. Bordin
 */
public class MetaDataValue implements Serializable {

    private final String name;
    private Object value;

    public Object getValue() {
        return value;
    }

    public MetaDataValue(MetaDataRef<?> propRef) {
        this.name = propRef.getName();
    }

    final void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
