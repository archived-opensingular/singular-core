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

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.opensingular.server.commons.config.IServerContext;

@Transactional
public interface SingularUserDetailsService extends UserDetailsService, UserDetailsContextMapper {


    @Override
    public default SingularUserDetails mapUserFromContext(DirContextOperations dirContextOperations, String s, Collection<? extends GrantedAuthority> collection) {
        return loadUserByUsername(s);
    }

    @Override
    public default void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
    }

    @Override
    public default SingularUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return loadUserByUsername(username, IServerContext.getContextFromRequest(request, getContexts()));
    }


    public SingularUserDetails loadUserByUsername(String username, IServerContext context) throws UsernameNotFoundException;

    public IServerContext[] getContexts();

    public List<SingularPermission> searchPermissions(String idUsuarioLogado);

}
