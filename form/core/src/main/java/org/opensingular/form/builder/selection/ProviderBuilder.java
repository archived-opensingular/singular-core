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

import java.io.Serializable;
import java.util.Arrays;

import org.opensingular.form.SInstance;
import org.opensingular.form.provider.LookupOptionsProvider;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.provider.SimpleProvider;
import org.opensingular.form.provider.TextQueryProvider;
import org.opensingular.form.SType;

public class ProviderBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance> extends AbstractBuilder {

    public ProviderBuilder(SType type) {
        super(type);
    }

    protected void provider(Provider<TYPE, ROOT_TYPE> provider) {
        type.asAtrProvider().provider(provider);
    }

    public void simpleProvider(SimpleProvider<TYPE, ROOT_TYPE> provider) {
        provider(provider);
    }

    public void filteredProvider(TextQueryProvider<TYPE, ROOT_TYPE> provider) {
        type.asAtrProvider().provider(provider);
    }

    @SafeVarargs
    public final void simpleProviderOf(TYPE... values) {
        type.asAtrProvider().provider((SimpleProvider<TYPE, ROOT_TYPE>) ins -> Arrays.asList(values));
    }

    public void provider(String provider) {
        type.asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <X extends Provider> void provider(Class<X> provider) {
        type.asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

}
