package br.net.mirante.singular.flow.core;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class ProcessDefinitionCache {

    private final ImmutableList<ProcessDefinition<?>> definitions;
    private final ImmutableMap<String, ProcessDefinition<?>> definitionsById;

    private static LoadingCache<Class<? extends ProcessDefinition<?>>, ProcessDefinition<?>> definitionsByClass = CacheBuilder
        .newBuilder().weakValues()
        .build(new CacheLoader<Class<? extends ProcessDefinition<?>>, ProcessDefinition<?>>() {
            @Override
            public ProcessDefinition<?> load(Class<? extends ProcessDefinition<?>> classeDefinicao) throws Exception {
                return classeDefinicao.newInstance();
            }
        });

    private static ProcessDefinitionCache cache;

    @SuppressWarnings("rawtypes")
    private ProcessDefinitionCache(String packageName) {
        ImmutableList.Builder<ProcessDefinition<?>> cache = ImmutableList.builder();
        Map<String, ProcessDefinition<?>> cacheById = new HashMap<>();
        Map<Class<? extends ProcessInstance>, ProcessDefinition<?>> cacheByInstanceType = new HashMap<>();

        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends ProcessDefinition>> subTypes = reflections.getSubTypesOf(ProcessDefinition.class);

        for (Class<? extends ProcessDefinition> classeDefinicao : subTypes) {
            if (Modifier.isAbstract(classeDefinicao.getModifiers())) {
                continue;
            }
            ProcessDefinition<?> def = getDefinition(classeDefinicao);
            cache.add(def);
            if (cacheById.containsKey(def.getKey())) {
                throw new SingularFlowException("Existe duas definições com a mesma sigla: " + def.getKey());
            }
            cacheById.put(def.getKey(), def);
            cacheByInstanceType.put(def.getProcessInstanceClass(), def);
        }
        definitions = cache.build();
        definitionsById = ImmutableMap.copyOf(cacheById);
    }

    public static ProcessDefinitionCache get(String packageName) {
        if (cache == null) {
            synchronized (ProcessDefinitionCache.class) {
                if (cache == null) {
                    cache = new ProcessDefinitionCache(packageName);
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

    public ProcessDefinition<?> getDefinition(String id) {
        return definitionsById.get(id);
    }

    public List<ProcessDefinition<?>> getDefinitions() {
        return definitions;
    }
}
