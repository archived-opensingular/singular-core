/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;

/**
 * Um WicketTester com configuração básicas do Singular para facilitar a criação de testes.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class SingularSimpleWicketTester extends AbstractWicketTester<AssertionsSimpleWComponent> {

    public SingularSimpleWicketTester() { }

    public SingularSimpleWicketTester(Class<? extends Page> homePage) {
        super(homePage);
    }

    public SingularSimpleWicketTester(WebApplication application) {
        super(application);
    }

    public SingularSimpleWicketTester(WebApplication application, String path) {
        super(application, path);
    }

    public SingularSimpleWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
    }

    public SingularSimpleWicketTester(WebApplication application, boolean init) {
        super(application, init);
    }

    public SingularSimpleWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
    }

    @Nonnull
    @Override
    public AssertionsSimpleWComponent toComponentAssertions(@Nullable Component component) {
        return new AssertionsSimpleWComponent(component);
    }
}
