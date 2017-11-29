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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.opensingular.form.SType;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.internal.lib.wicket.test.AbstractWicketTester;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;

/**
 * Um WicketTester com configuração básicas do Singular para facilitar a criação de testes.
 *
 * @author Daniel Bordin
 * @since 2017-02-12
 */
public class SingularWicketTester extends AbstractWicketTester<AssertionsWComponent> {

    public SingularWicketTester() {}

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck) {
        super(turnOnSerializationCheck);
    }

    public SingularWicketTester(Class<? extends Page> homePage) {
        super(homePage);
    }

    public SingularWicketTester(WebApplication application) {
        super(application);
    }

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(turnOnSerializationCheck, application);
    }

    public SingularWicketTester(WebApplication application, String path) {
        super(application, path);
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
    }

    public SingularWicketTester(WebApplication application, boolean init) {
        super(application, init);
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
    }

    @Nonnull
    @Override
    public AssertionsWComponent toComponentAssertions(@Nullable Component component) {
        return new AssertionsWComponent(component);
    }

    public final AssertionsSInstance getAssertionsInstance() {
        checkIfStartPageCalled();
        return getAssertionsPage().getSubComponentWithSInstance().assertSInstance();
    }

    public <T extends SType<?>> STypeTester<T> newSingularSTypeTester(Class<? extends T> sTypeClass){
        return new STypeTester<>(this, sTypeClass);
    }
}
