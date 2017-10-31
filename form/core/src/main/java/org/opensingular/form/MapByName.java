/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Representa um mapa que pode ser acesso por um nome ou uma classe que
 * representa esse nome.
 *
 * @author Daniel C. Bordin
 */

class MapByName<K> implements Iterable<K> {

    private final Function<K, String>        nameMapper;

    private final Map<String, K>             byName   = new LinkedHashMap<>();
    private final Map<Class<? extends K>, K> byClass = new HashMap<>();

    MapByName(Function<K, String> nameMapper) {
        this.nameMapper = nameMapper;
    }

    @SuppressWarnings("unchecked")
    public void add(@Nonnull K obj) {
        add(obj, (Class<K>) obj.getClass());
    }

    public void add(@Nonnull K obj, @Nullable Class<K> classToBeRegisterBy) {
        String name = getName(obj);
        byName.put(name, obj);
        if (classToBeRegisterBy != null) {
            byClass.put(classToBeRegisterBy, obj);
        }
    }

    @Nullable
    public <T extends K> T get(@Nonnull Class<T> targetClass) {
        K value = byClass.get(targetClass);
        return targetClass.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T extends K> T get(String name) {
        return (T) byName.get(name);
    }

    public Collection<K> getValues() {
        return byName.values();
    }

    @Nonnull
    final <T extends K> T getOrNewInstance(@Nonnull Class<T> targetClass) {
        T value = get(targetClass);
        if (value == null) {
            try {
                return targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SingularFormException("Erro instanciando " + targetClass.getName(), e);
            }
        }
        return value;
    }

    final <T extends K> void verifyMustNotBePresent(Class<T> targetClass, Object owner) {
        T value = get(targetClass);
        if (value != null) {
            throw new SingularFormException(errorMsg("A definição '" + getName(value) + "' já está carregada", owner));
        }
    }

    final void verifyMustNotBePresent(@Nonnull K newMember, Object owner) {
        verifyMustNotBePresent(getName(newMember), owner);
    }

    final void verifyMustNotBePresent(@Nonnull String fullName, Object owner) {
        if (byName.containsKey(fullName)) {
            throw new SingularFormException(errorMsg("A definição '" + fullName + "' já está criada", owner));
        }
    }

    private String errorMsg(String msg, Object owner) {
        if (owner instanceof  SDictionary) {
            return msg + " no dicionário";
        }
        return msg + " em " + owner;
    }

    @Nonnull
    private String getName(@Nonnull K val) {
        return nameMapper.apply(val);
    }

    @Override
    public Iterator<K> iterator() {
        return byName.values().iterator();
    }

}
