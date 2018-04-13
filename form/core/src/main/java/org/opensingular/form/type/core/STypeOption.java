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

package org.opensingular.form.type.core;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.provider.STypeOptionProvider;
import org.opensingular.form.type.core.SIOption.Option;

@SInfoType(name = "Option", spackage = SPackageCore.class)
public class STypeOption<SI extends SInstance> extends STypeComposite<SIOption<SI>> {

    public static final String FIELD_REF_ID      = "refId";
    public static final String FIELD_DESCRIPTION = "description";

    public STypeInteger        refId;
    public STypeString         description;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeOption() {
        super((Class) SIOption.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        this.refId = addFieldInteger(FIELD_REF_ID);
        this.description = addFieldString(FIELD_DESCRIPTION);
    }

    /**
     * Sets up a {@link STypeOptionProvider} that fetches options from a @{code SIList} field {@code optionsListField}.
     * @param optionsListField options source
     * @param displayFunction function that transforms the instance into the option's display string
     */
    public <STL extends STypeList<ST, SI>, ST extends SType<SI>> STypeOption<SI> withSelectionFromOptionProvider(
        STL optionsListField,
        Function<SI, String> displayFunction) {

        Function<SIOption<SI>, Collection<SI>> optionsFunc = it -> it.findNearest(optionsListField)
            .map(li -> li.stream().collect(toList()))
            .orElseGet(ArrayList::new);

        return withSelectionFromOptionProvider(optionsFunc, displayFunction);
    }

    /**
     * Sets up a {@link STypeOptionProvider} that fetches options from any source from the form instance.
     * @param optionsFunction returns instances for options
     * @param displayFunction function that transforms the instance into the option's display string
     */
    public STypeOption<SI> withSelectionFromOptionProvider(
        Function<SIOption<SI>, Collection<SI>> optionsFunction,
        Function<SI, String> displayFunction) {

        this.asAtrProvider()
            .idFunction(Option::getRefId)
            .displayFunction(Option::getDescription)
            .converter(SIOption.DEFAULT_CONVERTER)
            .provider(new STypeOptionProvider<SI>(optionsFunction, displayFunction));

        return this;
    }

    public STypeOption<SI> withOptions(Function<SIOption<SI>, Collection<SI>> optionsFunction) {
        getProvider().setOptionsFunction(optionsFunction);
        return this;
    }
    public STypeOption<SI> withDisplayFunction(Function<SI, String> descriptionFunction) {
        getProvider().setDescriptionFunction(descriptionFunction);
        return this;
    }

    @SuppressWarnings("unchecked")
    private STypeOptionProvider<SI> getProvider() {
        Provider<?, ?> provider = this.asAtrProvider().getProvider();
        if (!(provider instanceof STypeOptionProvider<?>))
            this.asAtrProvider().provider(new STypeOptionProvider<>());
        return (STypeOptionProvider<SI>) provider;
    }
}
