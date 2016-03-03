package br.net.mirante.singular.pet.server.spring.security;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import br.net.mirante.singular.pet.module.spring.security.ServerContext;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {


    protected abstract ServerContext getContext();


    protected String[] getDefaultPublicUrls() {
        return new String[]{"/rest/**", "/resources/**", "/index.html", getContext().getUrlPath() + "/wicket/resource/**"};
    }

}
