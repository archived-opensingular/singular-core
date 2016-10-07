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

package org.opensingular.form.document;

import org.opensingular.form.RefService;

import java.io.Serializable;
import java.util.Map;

/**
 * Service Registry which provides a ḿeans to register and lookup for services.
 * 
 * @author Fabricio Buzeto
 *
 */
public interface ServiceRegistry {

    @SuppressWarnings("serial")
    public static class Pair implements Serializable{
        final public Class<?>      type;
        final public RefService<?> provider;

        public Pair(Class<?> type, RefService<?> provider) {
            this.type = type;
            this.provider = provider;
        }
    }

    /**
     * List all factories for all registered services;
     * @return factory map.
     */
    Map<String, Pair> services();


    /**
     * Tries to find a service based on its class;
     *
     * @return <code>Null</code> if not found.
     */
    public <T> T lookupService(Class<T> targetClass);

    /**
     * Tries to find a service based on its name, casting to the desired type;
     *
     * @return <code>Null</code> if not found.
     */
    <T> T lookupService(String name, Class<T> targetClass);

    /**
     * Tries to find a service based on its name;
     * 
     * @return <code>Null</code> if not found.
     */
    Object lookupService(String name);
}
