package br.net.mirante.singular.pet.server.spring.security;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public abstract class AbstractSingularSpringSecurityAdapter extends WebSecurityConfigurerAdapter {


    /**
     * Caminho base da aplicação para peticionamento ou análise dependendo de onde essa
     * configuração de segurança está sendo utilizada. Geralmente trata-se do
     * path do filtro correspondente do wicket.
     *
     * @return Base path com wildcards ao final, não utilizar wildcards no início.
     */
    protected abstract String getBasePath();

    protected String getRegex() {
        return getBasePath().replaceAll("\\*", ".*");
    }

    protected String getPath() {
        String path = getBasePath().replace("*", "").replace(".", "").trim();
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    protected String[] getDefaultPublicUrls() {
        return new String[]{"/rest/**", "/resources/**", getPath() + "/wicket/resource/**"};
    }
}
