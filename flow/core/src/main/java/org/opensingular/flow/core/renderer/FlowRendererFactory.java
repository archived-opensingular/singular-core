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

package org.opensingular.flow.core.renderer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinition;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FlowRendererFactory {

    @SuppressWarnings("rawtypes")
    private static final LoadingCache<Class<? extends ProcessDefinition>, byte[]> cache =
            CacheBuilder.newBuilder().expireAfterWrite(4, TimeUnit.HOURS)
                    .build(new CacheLoader<Class<? extends ProcessDefinition>, byte[]>() {
                        @Override
                        public byte[] load(Class<? extends ProcessDefinition> classe) throws Exception {
                            return flowRenderer().generateImage(Flow.getProcessDefinition(classe));
                        }

                        private IFlowRenderer flowRenderer() {
                            return Flow.getConfigBean().getFlowRenderer();
                        }
                    });

    public static byte[] generateImageFor(ProcessDefinition<?> processDefinition) {
        try {
            return cache.get(processDefinition.getClass());
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Discards all entries in the cache.
     */
    public static synchronized void invalidateCache() {
        cache.invalidateAll();
    }
}
