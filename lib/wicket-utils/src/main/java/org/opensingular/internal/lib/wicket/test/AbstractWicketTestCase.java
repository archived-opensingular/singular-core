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

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTestCase;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;

import javax.annotation.Nonnull;

/**
 * Support class for creating JUnits test for Wicket.
 *
 * @author Daniel C. Bordin
 * @since 2017-11-02
 */
public abstract class AbstractWicketTestCase<WICKET_TESTER extends AbstractWicketTester> extends WicketTestCase {

    @Nonnull
    protected final WICKET_TESTER getTester() {
        return (WICKET_TESTER) super.tester;
    }

    @Nonnull
    protected abstract WICKET_TESTER createTester(WebApplication app);

    @Override
    protected final WicketTester newWicketTester(WebApplication app) {
        return createTester(app);
    }

    @Override
    protected WebApplication newApplication() {
        return new AdminApp();
    }

    private static class AdminApp extends MockApplication implements SingularAdminApp {
    }
}
