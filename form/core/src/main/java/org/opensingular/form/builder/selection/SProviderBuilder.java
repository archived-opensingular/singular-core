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

package org.opensingular.form.builder.selection;

import org.opensingular.form.provider.LookupOptionsProvider;
import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.SType;
import org.opensingular.form.provider.STextQueryProvider;

public class SProviderBuilder extends AbstractBuilder {

    public SProviderBuilder(SType type) {
        super(type);
    }

    public <T extends SSimpleProvider> void simpleProvider(Class<T> provider) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <T extends STextQueryProvider> void filteredProvider(Class<T> provider) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <T extends SSimpleProvider> void simpleProvider(String providerName) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(providerName));
    }

    public <T extends STextQueryProvider> void filteredProvider(String providerName) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(providerName));
    }

    public void simpleProvider(SSimpleProvider sSimpleProvider) {
        type.asAtrProvider().asAtrProvider().provider(sSimpleProvider);
    }

    public void filteredProvider(STextQueryProvider mapSimpleProvider) {
        type.asAtrProvider().asAtrProvider().provider(mapSimpleProvider);
    }

}