package br.net.mirante.singular.pet.server.spring.security;

import br.net.mirante.singular.pet.module.spring.security.ServerContext;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {


    protected abstract ServerContext getContext();


    protected String[] getDefaultPublicUrls() {
        return new String[]{"/rest/**", "/resources/**", getContext().getUrlPath() + "/wicket/resource/**"};
    }

}
