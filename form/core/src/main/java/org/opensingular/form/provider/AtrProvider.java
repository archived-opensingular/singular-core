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

package org.opensingular.form.provider;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SInstance;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.lib.commons.lambda.IFunction;

import java.io.Serializable;

public class AtrProvider extends STranslatorForAttribute {

    public AtrProvider() {
    }

    public AtrProvider(SAttributeEnabled target) {
        super(target);
    }

    public <T extends Serializable> AtrProvider filteredProvider(FilteredProvider<T> value) {
        return provider(value);
    }

    public <T extends Serializable, I extends SInstance> AtrProvider filteredOptionsProvider(TextQueryProvider<T, I> value) {
        return provider(value);
    }

    public <T extends Serializable, I extends SInstance> AtrProvider provider(Provider<T, I> value) {
        setAttributeValue(SPackageProvider.PROVIDER, value);
        return this;
    }

    public AtrProvider converter(SInstanceConverter value) {
        setAttributeValue(SPackageProvider.CONVERTER, value);
        return this;
    }

    public AtrProvider displayFunction(IFunction value) {
        setAttributeValue(SPackageProvider.DISPLAY_FUNCTION, value);
        return this;
    }

    public <T extends Serializable> IFunction<T, String> getDisplayFunction() {
        return getAttributeValue(SPackageProvider.DISPLAY_FUNCTION);
    }

    public <T extends Serializable, X > AtrProvider idFunction(IFunction<T, X> value) {
        setAttributeValue(SPackageProvider.ID_FUNCTION, value);
        return this;
    }

    public <T> IFunction<T, Object> getIdFunction() {
        return getAttributeValue(SPackageProvider.ID_FUNCTION);
    }

    public FilteredProvider getFilteredProvider() {
        return (FilteredProvider) getProvider();
    }

    public <T extends Serializable, S extends SInstance> Provider<T, S> getProvider() {
        return getAttributeValue(SPackageProvider.PROVIDER);
    }

    public SInstanceConverter getConverter() {
        return getAttributeValue(SPackageProvider.CONVERTER);
    }

}