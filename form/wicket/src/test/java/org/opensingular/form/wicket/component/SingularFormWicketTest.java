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

package org.opensingular.form.wicket.component;

import net.vidageek.mirror.dsl.Mirror;
import org.apache.wicket.Page;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmitter;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterConfig;

import javax.servlet.ServletException;
import java.util.Locale;

public class SingularFormWicketTest {


    @Before
    public void mockWicketContext() throws ServletException {
        ThreadContext t = ThreadContext.get(true);

        WebApplication webApplication = new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return Page.class;
            }


        };

        MockFilterConfig filterConfig = new MockFilterConfig(new MockServletContext(webApplication, null), "nada");
        WicketFilter filter = new WicketFilter(webApplication);
        filter.init(filterConfig);

        webApplication.setWicketFilter(filter);

        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getLocale()).thenReturn(Locale.ENGLISH);

        t.setSession(new WebSession(request));
        t.setRequestCycle(new RequestCycle(new RequestCycleContext(request, Mockito.mock(Response.class), Mockito.mock(IRequestMapper.class), Mockito.mock(IExceptionMapper.class))));
        t.setApplication(webApplication);

        webApplication.initApplication();
    }


    @Test
    public void invokePrivateMethodTest() throws Exception {
        SingularFormWicket f1 = Mockito.spy(new SingularFormWicket("1"){});

        Form f2 = new Form<>("12");
        f1.add(f2);

        IFormSubmitter formSubmitter = Mockito.mock(AjaxFormSubmitBehavior.AjaxFormSubmitter.class);
        Mockito.when(formSubmitter.getForm()).thenReturn(f2);

        new Mirror().on(f1).invoke().method("markFormsSubmitted").withArgs(formSubmitter);
        new Mirror().on(f2).invoke().method("markFormsSubmitted").withArgs(formSubmitter);

        f1.process(formSubmitter);
    }
}
