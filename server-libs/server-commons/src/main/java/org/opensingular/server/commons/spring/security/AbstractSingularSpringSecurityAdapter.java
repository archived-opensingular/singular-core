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

import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.inject.Inject;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Inject
    protected SingularServerConfiguration singularServerConfiguration;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(getDefaultPublicUrls());
    }


    protected abstract IServerContext getContext();


    public String[] getDefaultPublicUrls() {
        return singularServerConfiguration.getDefaultPublicUrls();
    }

}
