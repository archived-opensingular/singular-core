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

package org.opensingular.studio.app.init;

import org.apache.wicket.protocol.http.WicketFilter;
import org.opensingular.lib.support.spring.util.SingularAnnotationConfigWebApplicationContext;
import org.opensingular.studio.app.config.StudioAppConfig;
import org.opensingular.studio.core.config.StudioConfigProvider;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Nonnull;
import javax.servlet.*;
import java.util.EnumSet;

public class StudioWebAppInitializer implements WebApplicationInitializer {

    private StudioAppConfig studioConfig;

    public StudioWebAppInitializer() {
        studioConfig = (StudioAppConfig) StudioConfigProvider.get().retrieve();
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = createContext();
        configureContext(container, rootContext);
        addSpringMVCServlet(container, rootContext);
        addWicketFilter(container, rootContext);
        addSpringSecurityFilter(container, rootContext);
    }

    private void configureContext(ServletContext container, AnnotationConfigWebApplicationContext rootContext) {
        rootContext.setServletContext(container);
        container.addListener(new ContextLoaderListener(rootContext));
        studioConfig.getSpringAnnotatedConfigs().forEach(rootContext::register);
        rootContext.refresh();
    }

    private void addWicketFilter(ServletContext container, AnnotationConfigWebApplicationContext rootContext) {
        WicketFilter wicketFilter = new WicketFilter(rootContext.getBean(StudioApplication.class));
        wicketFilter.setFilterPath("");
        FilterRegistration.Dynamic filterRegistration = container.addFilter("wicketFilter", wicketFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "*");
    }

    @Nonnull
    private AnnotationConfigWebApplicationContext createContext() {
        return new SingularAnnotationConfigWebApplicationContext();
    }

    private void addSpringMVCServlet(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ServletRegistration.Dynamic dispatcher = ctx
                .addServlet("Spring MVC dispatcher servlet", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

    private void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }
}