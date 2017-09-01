package org.opensingular.studio.app.init;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.*;
import java.util.EventListener;


public class StudioWebAppInitializerTest {

    private ServletContext servletContext = new MockServletContext() {
        @Override
        public <T extends EventListener> void addListener(T t) {
        }

        @Override
        public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
            return Mockito.mock(ServletRegistration.Dynamic.class);
        }

        @Override
        public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
            return Mockito.mock(FilterRegistration.Dynamic.class);
        }

        @Override
        public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
            return Mockito.mock(FilterRegistration.Dynamic.class);
        }
    };

    @Test
    public void ok() throws Exception {
        new StudioWebAppInitializer().onStartup(servletContext);
    }
}