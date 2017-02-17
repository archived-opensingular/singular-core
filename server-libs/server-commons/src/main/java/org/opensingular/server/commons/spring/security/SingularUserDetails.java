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

package org.opensingular.server.commons.spring.security;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.opensingular.server.commons.config.IServerContext;

public interface SingularUserDetails extends UserDetails {

    default boolean isContext(IServerContext context) {
        return context.equals(getServerContext());
    }

    Object getUserPermissionKey();

    IServerContext getServerContext();

    String getDisplayName();

    List<SingularPermission> getPermissions();

    @Override
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    default String getPassword() {
        return null;
    }


    void addPermission(SingularPermission role);

    default void addPermissions(SingularPermission... roles) {
        addPermissions(Arrays.asList(roles));
    }

    default void addPermissions(List<SingularPermission> roles) {
        if (roles != null) {
            for (SingularPermission role : roles) {
                addPermission(role);
            }
        }
    }

    @Override
    default boolean isAccountNonExpired() {
        return true;
    }

    @Override
    default boolean isAccountNonLocked() {
        return true;
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    default boolean isEnabled() {
        return true;
    }
}
