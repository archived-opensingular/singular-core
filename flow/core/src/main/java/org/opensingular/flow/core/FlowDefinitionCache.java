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

package org.opensingular.flow.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class FlowDefinitionCache {

    private final ImmutableList<FlowDefinition<?>> definitions;
    private final ImmutableMap<String, FlowDefinition<?>> definitionsByKey;

    private static LoadingCache<Class<? extends FlowDefinition<?>>, FlowDefinition<?>> definitionsByClass = CacheBuilder
            .newBuilder().weakValues()
            .build(new CacheLoader<Class<? extends FlowDefinition<?>>, FlowDefinition<?>>() {
                @Override
                public FlowDefinition<?> load(Class<? extends FlowDefinition<?>> definitionClass) throws Exception {
                    return definitionClass.newInstance();
                }
            });

    private static FlowDefinitionCache cache;

    private final String[] packagesNames;

    @SuppressWarnings("rawtypes")
    private FlowDefinitionCache(String[] packagesNames) {
        this.packagesNames = packagesNames;
        ImmutableList.Builder<FlowDefinition<?>> newCache = ImmutableList.builder();
        Map<String, FlowDefinition<?>> cacheByKey = new HashMap<>();

        String[] packagesToScan = Arrays.copyOf(packagesNames, packagesNames.length + 2);
        packagesToScan[packagesToScan.length - 2] = "org.opensingular";
        packagesToScan[packagesToScan.length - 1] = "com.opensingular";

        Set<Class<? extends FlowDefinition>> subTypes = SingularClassPathScanner.get().findSubclassesOf(FlowDefinition.class,packagesToScan);

        for (Class<? extends FlowDefinition> definitionClass : subTypes) {
            if (Modifier.isAbstract(definitionClass.getModifiers()) || !hasEmptyConstructor(definitionClass)) {
                continue;
            }
            FlowDefinition<?> def = getDefinition(definitionClass);
            newCache.add(def);
            String key = def.getKey();
            if (cacheByKey.containsKey(key)) {
                throw new SingularFlowException("Existe duas definições com a mesma sigla: " + key);
            }
            cacheByKey.put(key, def);
        }

        definitions = newCache.build();
        definitionsByKey = ImmutableMap.copyOf(cacheByKey);

    }

    private boolean hasEmptyConstructor(Class<?> targetClass) {
        try {
            return targetClass.getConstructor() != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public synchronized static FlowDefinitionCache get(String[] packagesNames) {
        if (cache == null) {
            cache = new FlowDefinitionCache(packagesNames);
        }
        return cache;
    }

    /**
     * Discards all entries in the cache.
     */
    public static void invalidateAll() {
        synchronized (FlowDefinitionCache.class) {
            definitionsByClass.invalidateAll();
            cache = null;
        }
    }

    @Nonnull
    public static <T extends FlowDefinition<?>> T getDefinition(@Nonnull Class<T> definitionClass) {
        Objects.requireNonNull(definitionClass);
        FlowDefinition<?> def = definitionsByClass.getUnchecked(definitionClass);
        if (def == null) {
            throw new SingularFlowException(
                    "Não foi encontrada a definiçao de flow referente a classe " + definitionClass.getName());
        }
        return definitionClass.cast(def);
    }

    /**
     * @throws SingularFlowException <code> if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    public FlowDefinition<?> getDefinition(@Nonnull String key) {
        Objects.requireNonNull(key);
        FlowDefinition<?> flowDefinition = definitionsByKey.get(key);
        if (flowDefinition == null) {
            throw new SingularFlowException("O flow com chave '" + key + "' não foi encontrado nos pacotes: " +
                    Arrays.toString(packagesNames));
        }
        return flowDefinition;
    }

    /**
     * <code> this method does not throw a exception if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    public Optional<FlowDefinition<?>> getDefinitionOpt(@Nonnull String key) {
        Objects.requireNonNull(key);
        return Optional.ofNullable(definitionsByKey.get(key));
    }

    @Nonnull
    public List<FlowDefinition<?>> getDefinitions() {
        return definitions;
    }
}
