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

package org.opensingular.form.processor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Caches information about a Class that otherwise would be expsive do often be recalculated. It's intend to <b>INTERNAL
 * USE ONLY</b>.
 *
 * @author Daniel C. Bordin on 19/08/2017.
 */
public class ClassInspectionCache {

    private static Map<Class<?>, Object[]> classInfoCache = new HashMap<>();

    /**
     * Returns the associated  value the class e {@link CacheKey} informed. If not available, then calls the
     * provided function to calculate and caches the result.
     */
    @Nonnull
    public static <R> R getInfo(@Nonnull Class<?> target, @Nonnull CacheKey reference,
            @Nonnull Function<Class<?>, R> f) {
        Object[] caches = classInfoCache.computeIfAbsent(target, t -> new Object[CacheKey.values().length]);
        if (caches[reference.ordinal()] == null) {
            caches[reference.ordinal()] = f.apply(target);
        }
        return (R) caches[reference.ordinal()];
    }

    private synchronized static LoadingCache<Class<?>, Object[]> createCache() {
        return CacheBuilder.newBuilder().softValues().weakKeys().build(new CacheLoader<Class<?>, Object[]>() {
            @Override
            public Object[] load(Class<?> aClass) {
                return new Object[CacheKey.values().length];//NOSONAR
            }
        });
    }

    public enum CacheKey {HAS_ON_LOAD_TYPE_METHOD, PUBLIC_INFO, SIMPLE_NAME, FULL_NAME, FILE_DEFINITIONS}

}
