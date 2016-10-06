package org.opensingular.server.commons.config;

import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public interface SingularInitializer extends WebApplicationInitializer {

    public static final Logger logger = LoggerFactory.getLogger(SingularInitializer.class);
    static final String SINGULAR = "[SINGULAR] %s";
    static final String SERVLET_ATTRIBUTE_WEB_CONFIGURATION = "Singular-webInitializer";
    static final String SERVLET_ATTRIBUTE_SPRING_HIBERNATE_CONFIGURATION = "Singular-springHibernateInitializer";
    static final String SERVLET_ATTRIBUTE_FORM_CONFIGURATION_CONFIGURATION = "Singular-formInitializer";
    static final String SERVLET_ATTRIBUTE_FLOW_CONFIGURATION_CONFIGURATION = "Singular-flowInitializer";


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
                            .map(SpringHibernateInitializer::springMVCServletMapping)
                            .orElse(null),
                    Optional
                            .ofNullable(webInitializer)
                            .map(WebInitializer::serverContexts)
                            .orElse(ServerContext.values()));
        } else {
            logger.info(String.format(SINGULAR, " Null springSecurityInitializer, skipping Spring Security configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing FormConfiguration "));
        FormInitializer formInitializer = formConfiguration();
        if (formInitializer != null) {
            formInitializer.init(ctx, applicationContext);
        } else {
            logger.info(String.format(SINGULAR, " Null formInitializer, skipping Singular Form configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing FlowConfiguration "));
        FlowInitializer flowInitializer = flowConfiguration();
        if (flowInitializer != null) {
            flowInitializer.init(ctx, applicationContext);
        } else {
            logger.info(String.format(SINGULAR, " Null flowInitializer, skipping Singular Flow configuration"));
        }

        logger.info(String.format(SINGULAR, " Initializing SchedulerConfiguration "));
        SchedulerInitializer schedulerInitializer = schedulerConfiguration();
        if (schedulerInitializer != null) {
            schedulerInitializer.init(ctx, applicationContext);
        } else {
            logger.info(String.format(SINGULAR, " Null SchedulerInitializer, skipping Singular Scheduler configuration"));
        }

        if (applicationContext != null){
            applicationContext.register(SingularServerConfiguration.class);
            ctx.setAttribute(SERVLET_ATTRIBUTE_WEB_CONFIGURATION, webInitializer);
            ctx.setAttribute(SERVLET_ATTRIBUTE_SPRING_HIBERNATE_CONFIGURATION, springHibernateInitializer);
            ctx.setAttribute(SERVLET_ATTRIBUTE_FLOW_CONFIGURATION_CONFIGURATION, flowInitializer);
            ctx.setAttribute(SERVLET_ATTRIBUTE_FORM_CONFIGURATION_CONFIGURATION, formInitializer);
        }
    }



    public WebInitializer webConfiguration();

    public SpringHibernateInitializer springHibernateConfiguration();

    public FormInitializer formConfiguration();

    public FlowInitializer flowConfiguration();

    public SchedulerInitializer schedulerConfiguration();

    public SpringSecurityInitializer springSecurityConfiguration();

}
