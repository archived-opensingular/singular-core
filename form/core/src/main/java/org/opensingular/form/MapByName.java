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

import java.util.*;
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

    MapByName(Function<K, String> mapeadorNome) {
        this.nameMapper = mapeadorNome;
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

    public <T extends K> T get(Class<T> classeAlvo) {
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

    final <T extends K> T getOrNewInstance(Class<T> classeAlvo) {
        T valor = get(classeAlvo);
        if (valor == null) {
            return newInstance(classeAlvo);
        }
        return valor;
    }

    static <TT> TT newInstance(Class<TT> classeAlvo) {
        try {
            return classeAlvo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFormException("Erro instanciando " + classeAlvo.getName(), e);
        }
    }

    final <T extends K> void verifyMustNotBePresent(Class<T> classeAlvo, Object owner) {
        T valor = get(classeAlvo);
        if (valor != null) {
            throw new SingularFormException(erroMsg("A definição '" + getNome(valor) + "' já está carregada", owner));
        }
    }

    final void verifyMustNotBePresent(K alvo, Object owner) {
        verifyMustNotBePresent(getNome(alvo), owner);
    }

    final void verifyMustNotBePresent(String fullName, Object owner) {
        if (byName.containsKey(fullName)) {
            throw new SingularFormException(erroMsg("A definição '" + fullName + "' já está criada", owner));
        }
    }

    private String erroMsg(String msg, Object owner) {
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
