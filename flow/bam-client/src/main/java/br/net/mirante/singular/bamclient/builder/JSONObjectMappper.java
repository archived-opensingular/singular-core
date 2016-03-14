/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.builder;

import java.util.HashMap;
import java.util.Map;

import br.net.mirante.singular.bamclient.util.SelfReference;

public abstract class JSONObjectMappper<T extends JSONObjectMappper<T>> implements SelfReference<T> {

    final private Map<String, Object> objectMap = new HashMap<>();

    protected T put(String key, Object value) {
        this.objectMap.put(key, value);
        return self();
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

}
