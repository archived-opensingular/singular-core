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
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.studio.app.spring.StudioUserDetailsService;
import org.opensingular.studio.core.config.StudioConfig;
import org.opensingular.studio.core.wicket.StudioApplication;

import java.util.List;

public interface StudioAppConfig extends StudioConfig{

    /**
     * @return the Wicket Application
     */
    StudioApplication getWicketApplication();


    /**
     * Allow register another classes to be registered as spring config
     * @return the configs
     */
    List<Class<?>> getSpringAnnotatedConfigs();

    /**
     *
     */
    SingularSingletonStrategy getSingularSingletonStrategy();

    /**
     *
     */
    SDocumentFactory getSDocumentFactory();

    /**
     *
     */
    ServiceRegistry getServiceRegistry();

    StudioUserDetailsService getUserDetailsService();
}
