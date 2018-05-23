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

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeList;
import org.opensingular.form.provider.STypeFieldRefProvider;
import org.opensingular.form.type.core.SIFieldRef;
import org.opensingular.form.type.core.SIFieldRef.Option;
import org.opensingular.form.type.core.STypeFieldRef;
import org.opensingular.lib.commons.lambda.IFunction;

public class FieldRefSelectionBuilder<STL extends STypeList<ST, SI>, ST extends SType<SI>, SI extends SInstance> {

    private STypeFieldRef<SI> type;

    private FieldRefSelectionBuilder(STypeFieldRef<SI> type) {
        this.type = type;
    }

    public FieldRefSelectionBuilder(STypeFieldRef<SI> type, STL listField) {
        this(type);

        provider().setOptionsFunction(it -> it.findNearest(listField)
            .map(li -> li.stream().collect(toList()))
            .orElseGet(Collections::emptyList));
    }
    public FieldRefSelectionBuilder(STypeFieldRef<SI> type, IFunction<SIFieldRef<SI>, List<? extends SI>> optionsFunction) {
        this(type);
        provider().setOptionsFunction(optionsFunction);
    }

    public FieldRefSelectionBuilder<STL, ST, SI> display(IFunction<SI, String> displayFunction) {
        provider().setDescriptionFunction(displayFunction);
        return this;
    }
    public FieldRefSelectionBuilder<STL, ST, SI> display(SType<?> field) {
        return display(it -> defaultString(it.getField(field).toStringDisplay()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private STypeFieldRefProvider<SI> provider() {
        return Optional.ofNullable(type.asAtrProvider().getProvider())
            .filter(it -> it instanceof STypeFieldRefProvider)
            .map(it -> (STypeFieldRefProvider) it)
            .orElseGet(this::configureProvider);
    }

    private STypeFieldRefProvider<SI> configureProvider() {
        STypeFieldRefProvider<SI> provider = new STypeFieldRefProvider<>();
        type.asAtrProvider()
            .idFunction(Option::getRefId)
            .displayFunction(Option::getDescription)
            .converter(SIFieldRef.DEFAULT_CONVERTER)
            .provider(provider);
        return provider;
    }
}
