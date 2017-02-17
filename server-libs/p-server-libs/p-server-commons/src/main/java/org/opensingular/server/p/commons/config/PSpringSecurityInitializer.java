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

package org.opensingular.server.p.commons.config;

import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.config.SpringSecurityInitializer;
import org.opensingular.server.p.commons.spring.security.SecurityConfigs;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class PSpringSecurityInitializer extends SpringSecurityInitializer {

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigClass(IServerContext context) {
        if (context.equals(PServerContext.WORKLIST)) {
            return (Class<T>) SecurityConfigs.CASAnalise.class;
        } else if (context.equals(PServerContext.PETITION)) {
            return (Class<T>) SecurityConfigs.CASPeticionamento.class;
        } else if(context.equals(PServerContext.ADMINISTRATION)){
            return (Class<T>) SecurityConfigs.AdministrationSecuriry.class;
        }
        return null;
    }

}