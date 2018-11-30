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

package org.opensingular.form.spring;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.inject.Provider;
import java.io.Serializable;
import java.util.Optional;

public class UserDetailsProvider implements Serializable, Provider<UserDetails> {


    public UserDetails get() {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    public <T extends UserDetails> Optional<T> getOpt() {
        return Optional.ofNullable(getTyped());
    }

    public <T extends UserDetails> Optional<T> getOpt(Class<T> clazz) {
        return Optional.ofNullable(getTyped());
    }

    public <T extends UserDetails> T getTyped() {
        return (T) get();
    }


}