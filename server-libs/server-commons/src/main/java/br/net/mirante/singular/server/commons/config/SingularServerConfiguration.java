package br.net.mirante.singular.server.commons.config;


import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Bean para guardar parametros de configuração reutilizáveis
 * para a solução do singular
 */
public class SingularServerConfiguration implements ServletContextAware {


    private IServerContext[] contexts;
    private String springMVCServletMapping;
    private Map<String, Object> attrs = new HashMap<>();

    public IServerContext[] getContexts() {
        return contexts;
    }

    public String getSpringMVCServletMapping() {
        return springMVCServletMapping;
    }

    public Object setAttribute(String name, Object value) {
        return attrs.put(name, value);
    }

    public Object getAttribute(String name) {
        return attrs.get(name);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        WebInitializer webInitializer = (WebInitializer) servletContext.getAttribute(SingularInitializer.SERVLET_ATTRIBUTE_WEB_CONFIGURATION);
        SpringHibernateInitializer springHibernateInitializer = (SpringHibernateInitializer) servletContext.getAttribute(SingularInitializer.SERVLET_ATTRIBUTE_SPRING_HIBERNATE_CONFIGURATION);
        this.contexts = webInitializer.getServerContexts();
        this.springMVCServletMapping = springHibernateInitializer.getSpringMVCServletMapping();
    }
}
