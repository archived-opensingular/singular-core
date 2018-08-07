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

package org.opensingular.lib.commons.context;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public interface SingularSingletonStrategy {


    /**
     * Keeps a single instance and use the the given instance class as key
     * The key to recover the singleton is the object class
     * Only one instance per class can be registered
     * Null values must be ignored silently
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(@Nonnull T thisInstance);


    /**
     * Keeps a single instance for the given class and use the informed class as key
     * The key to recover the singleton is the object class
     * Only one instance per class can be registered
     * Null values must be ignored silently
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(Class<? super T> instanceClazz, T thisInstance);


    /**
     * Keep a single instance for the given name identifier
     * Note that, different from Spring singleton, in this case
     * Only one instance per name can be registered
     * Null values must be ignored silently
     * @param nameKey
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(String nameKey, T thisInstance);


    /**
     * Checks if exists a singleton for the given class
     * This method is intended to find singletons registered by
     * thie {SingularSingletonStrategy#put(T thisInstance)}
     *
     * @param classKey
     * @param <T>
     * @return true if exists, false otherwise
     */
    <T> boolean exists(Class<T> classKey);

    /**
     * Checks if exists a singleton for the given name
     * This method is intended to find singletons registered by
     * thie {SingularSingletonStrategy#put(String name, T thisInstance)}
     *
     * @param nameKey
     * @return true if exists, false otherwise
     */
    boolean exists(String nameKey);

    /**
     * @param singletonClass
     * @param <T>
     * @return
     */
    <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException;

    <T> T get(String name) throws SingularSingletonNotFoundException;

    /**
     * Get an exsisting singleton entry or create it if is necessary using @param singletonFactory
     * lambda
     * @param nameKey
     * @param singletonFactory
     * @param <T>
     * @return
     */
    @Nonnull
    <T> T singletonize(@Nonnull String nameKey, @Nonnull Supplier<T> singletonFactory);

    /**
     * 
     * Get an existing singleton entry or create it if is necessary using @param singletonFactory
     * lambda
     * @param classKey
     * @param singletonFactory
     * @param <T>
     * @return
     */
    @Nonnull
    <T> T singletonize(@Nonnull Class<T> classKey, @Nonnull Supplier<T> singletonFactory);

    /**
     * Returns all registered sigletons indexed by class or name string
     * @return
     */
    @Nonnull
    Map<Object, Object> getEntries();

    /**
     * keeps all entries passed by parameter inside its own storage
     * @param entries
     */
    void putEntries(@Nonnull SingularSingletonStrategy source);
}
