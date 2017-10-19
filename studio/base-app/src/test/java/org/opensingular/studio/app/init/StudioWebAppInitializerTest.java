/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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