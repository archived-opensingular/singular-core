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
    public void add(K novo) {
        add(novo, (Class<K>) novo.getClass());
    }

    public void add(K novo, Class<K> classeDeRegistro) {
        String nome = getNome(novo);
        byName.put(nome, novo);
        if (classeDeRegistro != null) {
            byClass.put(classeDeRegistro, novo);
        }
    }

    @Nullable
    public <T extends K> T get(@Nonnull Class<T> classeAlvo) {
        K valor = byClass.get(classeAlvo);
        return classeAlvo.cast(valor);
    }

    @SuppressWarnings("unchecked")
    public <T extends K> T get(String nome) {
        return (T) byName.get(nome);
    }

    public Collection<K> getValues() {
        return byName.values();
    }

    @Nonnull
    final <T extends K> T getOrNewInstance(@Nonnull Class<T> targetClass) {
        T valor = get(targetClass);
        if (valor == null) {
            try {
                return targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SingularFormException("Erro instanciando " + targetClass.getName(), e);
            }
        }
        return valor;
    }

    final <T extends K> void verifyMustNotBePresent(Class<T> classeAlvo, Object owner) {
        T valor = get(classeAlvo);
        if (valor != null) {
            throw new SingularFormException(errorMsg("A definição '" + getNome(valor) + "' já está carregada", owner));
        }
    }

    final void verifyMustNotBePresent(K newMember, Object owner) {
        verifyMustNotBePresent(getNome(newMember), owner);
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

    private String getNome(K val) {
        return nameMapper.apply(val);
    }

    @Override
    public Iterator<K> iterator() {
        return byName.values().iterator();
    }

}
