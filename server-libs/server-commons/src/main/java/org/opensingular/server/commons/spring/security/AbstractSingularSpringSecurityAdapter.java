package org.opensingular.server.commons.spring.security;

import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.inject.Inject;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Inject
    protected SingularServerConfiguration singularServerConfiguration;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(getDefaultPublicUrls());
    }


    protected abstract IServerContext getContext();


    public String[] getDefaultPublicUrls() {
        return singularServerConfiguration.getDefaultPublicUrls();
    }

}
