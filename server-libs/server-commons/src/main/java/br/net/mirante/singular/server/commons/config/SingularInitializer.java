package br.net.mirante.singular.server.commons.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Optional;

public interface SingularInitializer extends WebApplicationInitializer {

    public static final Logger logger = LoggerFactory.getLogger(SingularInitializer.class);
    static final String SINGULAR = "[SINGULAR] %s";
    static final String SERVLET_ATTRIBUTE_WEB_CONFIGURATION = "Singular-webInitializer";
    static final String SERVLET_ATTRIBUTE_SPRING_HIBERNATE_CONFIGURATION = "Singular-springHibernateInitializer";


    @Override
    default void onStartup(ServletContext ctx) throws ServletException {
        logger.info(String.format(SINGULAR, " Initializing Singular.... "));
        logger.info(String.format(SINGULAR, " Initializing WebConfiguration "));
        WebInitializer webInitializer = webConfiguration();
        if (webInitializer != null) {
            webConfiguration().init(ctx);
        } else {
            logger.info(String.format(SINGULAR, " Null webInitializer, skipping web configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing SpringHibernateConfiguration "));
        SpringHibernateInitializer springHibernateInitializer = springHibernateConfiguration();
        AnnotationConfigWebApplicationContext applicationContext = null;
        if (springHibernateInitializer != null) {
            applicationContext = springHibernateInitializer.init(ctx);
        } else {
            logger.info(String.format(SINGULAR, " Null springHibernateInitializer, skipping Spring configuration"));
        }
        logger.info(String.format(SINGULAR, " Initializing SpringSecurity "));
        SpringSecurityInitializer springSecurityInitializer = springSecurityConfiguration();
        if (springSecurityInitializer != null) {
            springSecurityConfiguration().init(ctx, applicationContext,
                    Optional
                            .ofNullable(springHibernateInitializer)
                            .map(SpringHibernateInitializer::getSpringMVCServletMapping)
                            .orElse(null),
                    Optional
                            .ofNullable(webInitializer)
                            .map(WebInitializer::getServerContexts)
                            .orElse(ServerContext.values()));
        } else {
            logger.info(String.format(SINGULAR, " Null springSecurityInitializer, skipping Spring Security configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing FormConfiguration "));
        FormInitializer formInitializer = formConfiguration();
        if (formInitializer != null) {
            formConfiguration().init(ctx, applicationContext);
        } else {
            logger.info(String.format(SINGULAR, " Null formInitializer, skipping Singular Form configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing FlowConfiguration "));
        FlowInitializer flowInitializer = flowConfiguration();
        if (flowInitializer != null) {
            flowConfiguration().init(ctx, applicationContext);
        } else {
            logger.info(String.format(SINGULAR, " Null flowInitializer, skipping Singular Flow configuration"));
        }


        if (applicationContext != null){
            applicationContext.register(SingularServerConfiguration.class);
            ctx.setAttribute(SERVLET_ATTRIBUTE_WEB_CONFIGURATION, webInitializer);
            ctx.setAttribute(SERVLET_ATTRIBUTE_SPRING_HIBERNATE_CONFIGURATION, springHibernateInitializer);
        }
    }



    public WebInitializer webConfiguration();

    public SpringHibernateInitializer springHibernateConfiguration();

    public FormInitializer formConfiguration();

    public FlowInitializer flowConfiguration();

    public SpringSecurityInitializer springSecurityConfiguration();

}
