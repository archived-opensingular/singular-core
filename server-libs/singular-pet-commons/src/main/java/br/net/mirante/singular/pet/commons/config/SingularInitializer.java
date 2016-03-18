package br.net.mirante.singular.pet.commons.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public interface SingularInitializer extends ServletContainerInitializer {

    public static final Logger logger = LoggerFactory.getLogger(SingularInitializer.class);

    @Override
    default void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info(" Initializing Singular.... ");
        logger.info(" Initializing WebConfiguration ");
        webConfiguration().init(ctx);
        logger.info(" Initializing SpringHibernateConfiguration ");
        SpringHibernateInitializer springHibernateInitializer = springHibernateConfiguration();
        AnnotationConfigWebApplicationContext applicationContext = springHibernateInitializer.init(ctx);
        logger.info(" Initializing SpringSecurity ");
        springSecurityConfiguration().init(ctx, applicationContext, springHibernateInitializer.getSpringMVCServletMapping());
        logger.info(" Initializing FormConfiguration ");
        formConfiguration().init(ctx, applicationContext);
        logger.info(" Initializing FlowConfiguration ");
        flowConfiguration().init(ctx, applicationContext);
    }

    public WebInitializer webConfiguration();

    public SpringHibernateInitializer springHibernateConfiguration();

    public FormInitializer formConfiguration();

    public FlowInitializer flowConfiguration();

    public SpringSecurityInitializer springSecurityConfiguration();

}
