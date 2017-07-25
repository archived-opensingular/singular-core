/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.internal.lib.wicket.test;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;

/**
 * Um WicketTester com configuração básicas facilitar a criação de testes com Wicket.
 *
 * @author Daniel C. Bordin on 23/07/2017.
 */
public class SingularBasicWicketTester extends WicketTester {

    public SingularBasicWicketTester() {
        setUp();
    }

    public SingularBasicWicketTester(Class<? extends Page> homePage) {
        super(homePage);
        setUp();
    }

    public SingularBasicWicketTester(WebApplication application) {
        super(application);
        setUp();
    }

    @Deprecated
    public SingularBasicWicketTester(boolean turnOnSerializationCheck) {
        setUp(turnOnSerializationCheck);
    }

    @Deprecated
    public SingularBasicWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(application);
        setUp(turnOnSerializationCheck);
    }

    public SingularBasicWicketTester(WebApplication application, String path) {
        super(application, path);
        setUp();
    }

    public SingularBasicWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
        setUp();
    }

    public SingularBasicWicketTester(WebApplication application, boolean init) {
        super(application, init);
        setUp();
    }

    public SingularBasicWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
        setUp();
    }

    private void setUp() {
        setUp(true);
    }

    private void setUp(boolean turnOnSerializationCheck) {
        getApplication().getMarkupSettings().setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        if (turnOnSerializationCheck) {
            WicketSerializationDebugUtil.configurePageSerializationDebug(getApplication(), getClass());
        }
    }

}
