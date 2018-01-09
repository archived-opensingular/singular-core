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

package org.opensingular.studio.app.config;

import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.spring.SpringSDocumentFactoryEmpty;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.context.spring.SpringServiceRegistry;
import org.opensingular.lib.context.singleton.SpringBoundedSingletonStrategy;
import org.opensingular.studio.app.spring.DefaulSpringSecurityConfig;
import org.opensingular.studio.app.spring.StudioPersistenceConfiguration;
import org.opensingular.studio.app.spring.StudioSpringConfiguration;
import org.opensingular.studio.app.spring.StudioUserDetailsService;
import org.opensingular.studio.app.spring.StudioWebConfiguration;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStudioAppConfig implements StudioAppConfig {

    @Override
    public StudioApplication getWicketApplication() {
        return new StudioApplication(this);
    }

    @Override
    public List<Class<?>> getSpringAnnotatedConfigs() {
        List<Class<?>> springConfigs = new ArrayList<>();
        springConfigs.add(getSpringConfig());
        springConfigs.add(getSpringWebConfig());
        springConfigs.add(getSpringPersistenceConfig());
        springConfigs.add(getSpringSecurityConfig());
        return springConfigs;
    }

    @Override
    public SingularSingletonStrategy getSingularSingletonStrategy() {
        return new SpringBoundedSingletonStrategy();
    }

    @Override
    public SDocumentFactory getSDocumentFactory() {
        return new SpringSDocumentFactoryEmpty();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return new SpringServiceRegistry();
    }

    public Class<? extends StudioSpringConfiguration> getSpringConfig() {
        return StudioSpringConfiguration.class;
    }

    public Class<? extends WebSecurityConfigurerAdapter> getSpringSecurityConfig() {
        return DefaulSpringSecurityConfig.class;
    }

    public Class<? extends WebMvcConfigurerAdapter> getSpringWebConfig() {
        return StudioWebConfiguration.class;
    }

    public Class<? extends StudioPersistenceConfiguration> getSpringPersistenceConfig() {
        return StudioPersistenceConfiguration.class;
    }

    @Override
    public StudioUserDetailsService getUserDetailsService() {
        return new StudioUserDetailsService();
    }
}