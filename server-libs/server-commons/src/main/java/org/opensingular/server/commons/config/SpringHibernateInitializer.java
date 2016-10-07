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

package org.opensingular.server.commons.config;

import org.opensingular.server.commons.spring.SingularDefaultBeanFactory;
import org.opensingular.server.commons.spring.SingularDefaultPersistenceConfiguration;
import org.opensingular.server.commons.spring.SingularServerSpringAppConfig;
import org.opensingular.server.commons.spring.SpringFoxSwaggerConfig;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Optional;

public class SpringHibernateInitializer {

    public static final String SPRING_MVC_DISPATCHER_SERVLET = "Spring MVC Dispatcher Servlet";

    public SpringHibernateInitializer() {

    }

    public AnnotationConfigWebApplicationContext init(ServletContext ctx) {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(springConfigurationClass());
        Optional.ofNullable(swaggerConfig()).ifPresent(applicationContext::register);
        applicationContext.register(beanFactory());
        Optional.ofNullable(persistenceConfiguration()).ifPresent(applicationContext::register);
        addSpringContextListener(ctx, applicationContext);
        addSpringRequestContextListener(ctx, applicationContext);
        addSpringMVCServlet(ctx, applicationContext);
        return applicationContext;
    }

    protected Class<? extends SpringFoxSwaggerConfig> swaggerConfig() {
        return SpringFoxSwaggerConfig.class;
    }

    protected void addSpringContextListener(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx.addListener(new ContextLoaderListener(applicationContext));
    }

    protected void addSpringRequestContextListener(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx.addListener(RequestContextListener.class);
    }

    protected void addSpringMVCServlet(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        //Servlet
        ServletRegistration.Dynamic dispatcher = ctx.addServlet(SPRING_MVC_DISPATCHER_SERVLET, new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(springMVCServletMapping());
    }

    /**
     * Fornece a classe que será utilizada como configuração java do Spring.
     * A classe fornecida deve herdar de {@link SingularServerSpringAppConfig} e deve
     * ser anotada com {@link org.springframework.context.annotation.Configuration}.
     * As principais configurações do pet server são feitas pela superclasse bastando declarar
     * na classe informada apenas as configurações e beans do spring específicos da aplicação
     *
     * @return Uma classe concreta que herda de {@link SingularServerSpringAppConfig} e anotada com {@link org.springframework.context.annotation.Configuration}
     */

    protected Class<? extends SingularServerSpringAppConfig> springConfigurationClass() {
        return SingularServerSpringAppConfig.class;
    }


    protected Class<? extends SingularDefaultBeanFactory> beanFactory() {
        return SingularDefaultBeanFactory.class;
    }

    protected Class<? extends SingularDefaultPersistenceConfiguration> persistenceConfiguration() {
        return SingularDefaultPersistenceConfiguration.class;
    }

    protected String springMVCServletMapping() {
        return "/*";
    }

}
