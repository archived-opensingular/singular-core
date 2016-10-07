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
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProcessDefinitionCache {

    private final ImmutableList<ProcessDefinition<?>> definitions;
    private final ImmutableMap<String, ProcessDefinition<?>> definitionsByKey;

    private static LoadingCache<Class<? extends ProcessDefinition<?>>, ProcessDefinition<?>> definitionsByClass = CacheBuilder
        .newBuilder().weakValues()
        .build(new CacheLoader<Class<? extends ProcessDefinition<?>>, ProcessDefinition<?>>() {
            @Override
            public ProcessDefinition<?> load(Class<? extends ProcessDefinition<?>> classeDefinicao) throws Exception {
                return classeDefinicao.newInstance();
            }
        });

    private static ProcessDefinitionCache cache;

    private final String[] packagesNames;
    
    @SuppressWarnings("rawtypes")
    private ProcessDefinitionCache(String[] packagesNames) {
        this.packagesNames = packagesNames;
        ImmutableList.Builder<ProcessDefinition<?>> cache = ImmutableList.builder();
        Map<String, ProcessDefinition<?>> cacheByKey = new HashMap<>();
        Map<Class<? extends ProcessInstance>, ProcessDefinition<?>> cacheByInstanceType = new HashMap<>();

        Reflections reflections = new Reflections(packagesNames);

        Set<Class<? extends ProcessDefinition>> subTypes = reflections.getSubTypesOf(ProcessDefinition.class);

        for (Class<? extends ProcessDefinition> classeDefinicao : subTypes) {
            if (Modifier.isAbstract(classeDefinicao.getModifiers())) {
                continue;
            }
            ProcessDefinition<?> def = getDefinition(classeDefinicao);
            cache.add(def);
            if (cacheByKey.containsKey(def.getKey())) {
                throw new SingularFlowException("Existe duas definições com a mesma sigla: " + def.getKey());
            }
            cacheByKey.put(def.getKey(), def);
            cacheByInstanceType.put(def.getProcessInstanceClass(), def);
        }
        definitions = cache.build();
        definitionsByKey = ImmutableMap.copyOf(cacheByKey);
    }

    public static ProcessDefinitionCache get(String[] packagesNames) {
        if (cache == null) {
            synchronized (ProcessDefinitionCache.class) {
                if (cache == null) {
                    cache = new ProcessDefinitionCache(packagesNames);
                }
            }
        }
        return cache;
    }

    /**
     * Discards all entries in the cache.
     */
    public static void invalidateAll() {
        synchronized (ProcessDefinitionCache.class) {
            definitionsByClass.invalidateAll();
            cache = null;
        }
    }

    public static <T extends ProcessDefinition<?>> T getDefinition(Class<T> definitionClass) {
        ProcessDefinition<?> def = definitionsByClass.getUnchecked(definitionClass);
        return definitionClass.cast(def);
    }

    /**
     * @throws SingularFlowException <code> if there is no ProcessDefinition associated with key</code>
     */
    public ProcessDefinition<?> getDefinition(String key) {
        ProcessDefinition<?> processDefinition = definitionsByKey.get(key);
        if(processDefinition == null){
            throw new SingularFlowException("O processo com chave '" + key + "' não foi encontrado nos pacotes: " + packagesNames);
        }
        return processDefinition;
    }

    /**
     * <code> this method does not throw a exception if there is no ProcessDefinition associated with key</code>
     * @param key
     * @return
     */
    public ProcessDefinition<?> getDefinitionUnchecked(String key) {
        return definitionsByKey.get(key);
    }

    public List<ProcessDefinition<?>> getDefinitions() {
        return definitions;
    }
}
