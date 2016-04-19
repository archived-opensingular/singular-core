package br.net.mirante.singular.server.commons.spring.security;

import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.config.ServerContext;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {


    protected abstract IServerContext getContext();


    protected String[] getDefaultPublicUrls() {
        return new String[]{"/rest/**", "/resources/**", "/index.html", getContext().getUrlPath() + "/wicket/resource/**"};
    }

}
