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

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.converter.AutoSICompositeConverter;
import org.opensingular.form.converter.EnumSInstanceConverter;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.converter.SimpleSInstanceConverter;

import java.io.Serializable;


public class ConverterBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public ConverterBuilder(SType type) {
        super(type);
    }

    public <X extends Enum<X>> ProviderBuilder<TYPE, ROOT_TYPE> enumConverter(Class<X> enumClass) {
        type.asAtrProvider().converter(new EnumSInstanceConverter<>(enumClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> converter(SInstanceConverter<TYPE, ELEMENT_TYPE> converter) {
        type.asAtrProvider().converter(converter);
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> autoConverterOf(Class resultClass) {
        type.asAtrProvider().converter(AutoSICompositeConverter.of(resultClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> simpleConverter() {
        type.asAtrProvider().converter(new SimpleSInstanceConverter<>());
        return next();
    }

    private ProviderBuilder<TYPE, ROOT_TYPE> next() {
        return new ProviderBuilder<>(type);
    }

}