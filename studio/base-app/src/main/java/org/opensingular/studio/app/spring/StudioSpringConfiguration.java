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

package org.opensingular.studio.app.spring;

import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.opensingular.studio.app.config.StudioAppConfig;
import org.opensingular.studio.core.config.StudioConfigProvider;
import org.opensingular.studio.core.menu.StudioMenu;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = {"org.opensingular.lib.support.spring.util",
        "org.opensingular.studio.app",
        "com.opensingular"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION,
                        value = AutoScanDisabled.class)
        })
public class StudioSpringConfiguration implements Loggable {
    private final StudioAppConfig studioConfig;

    public StudioSpringConfiguration() {
        studioConfig = (StudioAppConfig) StudioConfigProvider.get().retrieve();
    }

    @Bean
    public StudioMenu studioMenu() {
        return studioConfig.getAppMenu();
    }

    @Bean
    public StudioApplication studioApplication() {
        return studioConfig.getWicketApplication();
    }

    @Bean
    public SingularSingletonStrategy singularSingletonStrategy() {
        return studioConfig.getSingularSingletonStrategy();
    }

    @Bean
    public SDocumentFactory sDocumentFactory() {
        return studioConfig.getSDocumentFactory();
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return studioConfig.getServiceRegistry();
    }

    @Bean
    public StudioUserDetailsService userDetailsService(){
        return studioConfig.getUserDetailsService();
    }
}