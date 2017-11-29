/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.support.spring.security;

import org.opensingular.lib.commons.base.SingularProperties;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultRestUserDetailsService implements RestUserDetailsService {

    public static final String SUBJECT_PRINCIPAL_REGEX = "CN=(.*?)(?:,|$)";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (getAllowedCommonName().equals(username)) {
            return createUserDetails(username);
        }
        throw new UsernameNotFoundException("Não foi possivel autenticar o certificado informado");
    }

    protected UserDetails createUserDetails(String username){
        return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }

    @Override
    public String getSubjectPrincipalRegex() {
        return SUBJECT_PRINCIPAL_REGEX;
    }

    protected String getAllowedCommonName() {
        return SingularProperties.get().getProperty(SingularProperties.REST_ALLOWED_COMMON_NAME, "");
    }

}