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

import org.opensingular.form.SingularFormException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service Registry which provides a ḿeans to register and lookup for services.
 * 
 * @author Fabricio Buzeto
 *
 */
public interface ServiceRegistry {

    /** Tries to find a service based on its class; */
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass);

    @Nonnull
    public default <T> T lookupServiceOrException(@Nonnull Class<T> targetClass) {
        return lookupService(targetClass).orElseThrow(
                () -> new SingularFormException("Bean of class " + targetClass + " not found"));
    }

    /** Tries to find a service based on its name, casting to the desired type; */
    @Nonnull
    <T> Optional<T> lookupService(@Nonnull String name, @Nonnull Class<T> targetClass);

    /** Tries to find a service based on its name; */
    @Nonnull
    Optional<Object> lookupService(@Nonnull String name);
}
