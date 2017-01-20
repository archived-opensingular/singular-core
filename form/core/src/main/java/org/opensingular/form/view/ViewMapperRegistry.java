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

package org.opensingular.form.view;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.lambda.ISupplier;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * Criar um registro cruzando MTipo e MView. Ao solicitar a view para um dado
 * tipo, procura a mais próxima (ou adequada) de acordo com as informações
 * passadas.
 * </p>
 * <p>
 * Em geral é usado internamente pelo geradores de interface para encontrar o
 * gerados de um dado tipo e view.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class ViewMapperRegistry<T> implements Serializable {

    private final HashMap<Class<? extends SType>, List<RegisterEntry<T>>> registry = new HashMap<>();

    /**
     * Registra o fornecedor para o tipo para quando não for solicitado um view
     * especifica. Seria a factory default.
     */
    public ViewMapperRegistry<T> register(Class<? extends SType> type, ISupplier<T> factory) {
        return register(type, null, factory);
    }

    /**
     * Registra o fornecedor default (se viewType == null) ou o fornecedor
     * específico para uma view em particular.
     * @param viewType Pode ser null
     */
    public ViewMapperRegistry<T> register(Class<? extends SType> type, Class<? extends SView> viewType, ISupplier<T> factory) {
        Objects.requireNonNull(factory);
        List<RegisterEntry<T>> list = registry.get(Objects.requireNonNull(type));
        if (list == null) {
            list = new ArrayList<>(1);
            registry.put(type, list);
        }
        list.add(new RegisterEntry<>(viewType, factory, 100));
        return this;
    }

    /**
     * <p>
     * Tenta encontrar o dados mais adequado a instancia e view informados.
     * </p>
     * <p>
     * Faz a busca na seguinte ordem:
     * <ul>
     * <li>Procura para o tipo da intância se existe algum registro para a view
     * informada (se view != null).</li>
     * <li>Não encontrando, procura nos tipos pai do tipo se há um registro para
     * a view informada (se view != null)</li>
     * <li>Não encontrando, começa a procura do inicio (do tipo da instância),
     * mas agora procurando pela view default.</li>
     * <li>Não encontrando, avança na procura da view default a partir do tipo
     * pai.</li>
     * </ul>
     * </p>
     *
     * @param view
     *            Pode ser null
     */
    public Optional<T> getMapper(SInstance instance, SView view) {
        Class<? extends SType> type = instance.getType().getClass();
        if (view.getClass() == SView.class) {
            view = null;
        }
        T mapper = getMapper(type, view);
        if (mapper == null && view != null) {
            mapper = getMapper(type, null);
        }
        return Optional.ofNullable(mapper);
    }

    private T getMapper(Class<?> type, SView view) {
        while (type != SType.class) {
            List<RegisterEntry<T>> list = registry.get(type);
            if (list != null) {
                T selected = findEntryMoreRelevant(list, view);
                if (selected != null) {
                    return selected;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private T findEntryMoreRelevant(List<RegisterEntry<T>> list, SView view) {
        int score = -1;
        RegisterEntry<T> selected = null;
        for (RegisterEntry<T> entry : list) {
            if (entry.isCompatible(view)) {
                int newScore = entry.scoreFor(view);
                if (selected == null || newScore > score) {
                    selected = entry;
                    score = newScore;
                }
            }
        }
        return selected == null ? null : selected.factory.get();
    }

    /**
     * Representa um mapeamento de View e suas respectiva factory para um tipo
     * específico.
     *
     * @author Daniel C. Bordin
     */
    private static final class RegisterEntry<T> implements Serializable {
        final Class<? extends SView> view;
        final ISupplier<T> factory;
        final int priority;

        RegisterEntry(Class<? extends SView> view, ISupplier<T> factory, int priority) {
            this.view = view;
            this.factory = factory;
            this.priority = priority;
        }

        public int scoreFor(SView target) {
            int score = priority * 100;
            if (target != null) {
                Class<?> v = view;
                while (v != target.getClass()) {
                    score--;
                    v = v.getSuperclass();
                }
            }
            return score;
        }

        public boolean isCompatible(SView target) {
            if (target == null) {
                return view == null;
            } else if (view != null) {
                return target.getClass().isAssignableFrom(view);
            }
            return false;
        }

    }

}
