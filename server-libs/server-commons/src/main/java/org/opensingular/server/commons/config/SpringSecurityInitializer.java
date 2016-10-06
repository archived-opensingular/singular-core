package org.opensingular.server.commons.config;

import org.opensingular.server.commons.spring.security.config.SingularLogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;

public abstract class SpringSecurityInitializer {

    static final String SINGULAR_SECURITY = "[SINGULAR][SECURITY] %s";
    public static final Logger logger = LoggerFactory.getLogger(SpringSecurityInitializer.class);

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext, String springMVCServletMapping, IServerContext[] serverContexts) {
        addSpringSecurityFilter(ctx, applicationContext, springMVCServletMapping);
        for (IServerContext context : serverContexts) {
            logger.info(String.format(SINGULAR_SECURITY, "Securing (Spring Security) context: "+context.getContextPath()));
            applicationContext.register(getSpringSecurityConfigClass(context));
            addLogoutFilter(ctx, applicationContext, springMVCServletMapping, context);
        }
    }


    protected void addLogoutFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext, String springMVCServletMapping, IServerContext context) {
        ctx
                .addFilter("singularLogoutFilter" + System.identityHashCode(context), SingularLogoutFilter.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, context.getUrlPath() + "/logout");
    }

    protected void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext, String springMVCServletMapping) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, springMVCServletMapping);
    }


    protected abstract <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigClass(IServerContext context);

}
