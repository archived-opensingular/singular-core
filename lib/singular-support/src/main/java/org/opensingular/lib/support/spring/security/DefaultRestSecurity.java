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

import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;

@EnableWebMvc
@Configuration
@AutoScanDisabled
@Order(99)
public class DefaultRestSecurity extends WebSecurityConfigurerAdapter {

    @Inject
    private RestUserDetailsService restUserDetailsService;

    public static final String REST_ANT_PATTERN = "/rest/**";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher(REST_ANT_PATTERN)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .x509()
                .subjectPrincipalRegex(restUserDetailsService.getSubjectPrincipalRegex())
                .userDetailsService(restUserDetailsService);

    }

}