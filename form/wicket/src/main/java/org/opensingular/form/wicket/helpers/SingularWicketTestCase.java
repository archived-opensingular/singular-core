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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.protocol.http.WebApplication;
import org.opensingular.internal.lib.wicket.test.AbstractWicketTestCase;

import javax.annotation.Nonnull;

/**
 * Support class for creating JUnits test for Wicket.
 *
 * @author Daniel C. Bordin
 * @since 2017-10-28
 */
public class SingularWicketTestCase extends AbstractWicketTestCase<SingularWicketTester> {

    @Nonnull
    @Override
    protected SingularWicketTester createTester(WebApplication app) {
        return new SingularWicketTester(app);
    }
}
