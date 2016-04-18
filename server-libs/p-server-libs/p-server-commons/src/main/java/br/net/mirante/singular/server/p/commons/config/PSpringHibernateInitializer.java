package br.net.mirante.singular.server.p.commons.config;

import br.net.mirante.singular.server.commons.config.SpringHibernateInitializer;
import br.net.mirante.singular.server.commons.spring.PetServerSpringAppConfig;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public abstract class PSpringHibernateInitializer extends SpringHibernateInitializer {


}
