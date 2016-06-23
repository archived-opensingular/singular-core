/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BoxModel implements Map<String, Object>, Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> intern;

    public BoxModel(Map<String, Object> map) {
        intern = new HashMap<>(map);
    }

    public Long getCod() {
        return ((Integer) intern.get("cod")).longValue();
    }

    @Override
    public int size() {
        return intern.size();
    }

    @Override
    public boolean isEmpty() {
        return intern.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return intern.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return intern.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return intern.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return intern.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return intern.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        intern.putAll(m);
    }

    @Override
    public void clear() {
        intern.clear();
    }

    @Override
    public Set<String> keySet() {
        return intern.keySet();
    }

    @Override
    public Collection<Object> values() {
        return intern.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return intern.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return intern.equals(o);
    }

    @Override
    public int hashCode() {
        return intern.hashCode();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return intern.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        intern.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        intern.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return intern.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return intern.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return intern.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return intern.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return intern.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return intern.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return intern.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return intern.merge(key, value, remappingFunction);
    }
}