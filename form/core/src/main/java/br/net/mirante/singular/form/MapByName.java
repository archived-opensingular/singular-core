/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

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

    final static <TT extends Object> TT newInstance(Class<TT> classeAlvo) {
        try {
            return classeAlvo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro instanciando " + classeAlvo.getName(), e);
        }
    }

    final <T extends K> void verifyMustNotBePresent(Class<T> classeAlvo) {
        T valor = get(classeAlvo);
        if (valor != null) {
            throw new RuntimeException("A definição '" + getNome(valor) + "' já está carregada");
        }
    }

    final void verifyMustNotBePresent(K alvo) {
        verifyMustNotBePresent(getNome(alvo));
    }

    final void verifyMustNotBePresent(String fullName) {
        if (byName.containsKey(fullName)) {
            throw new SingularFormException("A definição '" + fullName + "' já está criada");
        }
    }

    private String getNome(K val) {
        return nameMapper.apply(val);
    }

    @Override
    public Iterator<K> iterator() {
        return byName.values().iterator();
    }

}
