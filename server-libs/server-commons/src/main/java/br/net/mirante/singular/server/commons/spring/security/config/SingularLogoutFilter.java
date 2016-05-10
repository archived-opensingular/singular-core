package br.net.mirante.singular.server.commons.spring.security.config;

import br.net.mirante.singular.commons.util.Loggable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SingularLogoutFilter implements Filter, Loggable {

    private FilterConfig filterConfig;

    public SingularLogoutFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            SingularLogoutHandler singularLogoutHandler = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(SingularLogoutHandler.class);
            singularLogoutHandler.handleLogout((HttpServletRequest) req, (HttpServletResponse) resp);
        } catch (NoSuchBeanDefinitionException e) {
            getLogger().info("Não há  bean "+SingularLogoutHandler.class.getSimpleName()+" disponível no cotexto ignorando singular logout ");
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
