package br.net.mirante.singular.server.p.commons.config;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;

public abstract class SpringSecurityInitializer {

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext, String springMVCServletMapping) {
        addSpringSecurityFilter(ctx, applicationContext, springMVCServletMapping);
        applicationContext.register(getSpringSecurityConfigAnaliseClass());
        applicationContext.register(getSpringSecurityConfigPeticionamentoClass());
    }

    protected void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext, String springMVCServletMapping) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, springMVCServletMapping);
    }


    protected abstract <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigAnaliseClass();

    protected abstract <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigPeticionamentoClass();

}
