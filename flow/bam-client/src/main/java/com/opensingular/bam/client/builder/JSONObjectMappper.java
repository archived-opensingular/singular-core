/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder;

import java.util.HashMap;
import java.util.Map;

import com.opensingular.bam.client.util.SelfReference;

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
