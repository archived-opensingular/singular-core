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

import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.SIFieldRef;
import org.opensingular.form.type.core.SIFieldRef.Option;
import org.opensingular.lib.commons.lambda.IFunction;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public final class STypeFieldRefProvider<SI extends SInstance>
    implements Provider<SIFieldRef.Option, SIFieldRef<SI>> {

    private IFunction<SIFieldRef<SI>, List<? extends SI>> optionsFunction     = it -> null;
    private IFunction<SI, String>                         descriptionFunction = SInstance::toStringDisplay;

    public STypeFieldRefProvider() {}
    public STypeFieldRefProvider(IFunction<SIFieldRef<SI>, List<? extends SI>> optionsFunction, IFunction<SI, String> descriptionFunction) {
        setOptionsFunction(optionsFunction);
        setDescriptionFunction(descriptionFunction);
    }
    @Override
    public List<Option> load(ProviderContext<SIFieldRef<SI>> context) {
        List<? extends SI> list = getOptionsFunction().apply(context.getInstance());
        Stream<? extends SI> stream = (list != null) ? list.stream() : Stream.empty();
        return stream
            .map(it -> new SIFieldRef.Option(it.getId(), getDescriptionFunction().apply(it)))
            .collect(toList());
    }

    public Function<SIFieldRef<SI>, List<? extends SI>> getOptionsFunction() {
        return optionsFunction;
    }
    public Function<SI, String> getDescriptionFunction() {
        return descriptionFunction;
    }
    public STypeFieldRefProvider<SI> setOptionsFunction(IFunction<SIFieldRef<SI>, List<? extends SI>> optionsFunction) {
        this.optionsFunction = optionsFunction;
        return this;
    }
    public STypeFieldRefProvider<SI> setDescriptionFunction(IFunction<SI, String> descriptionFunction) {
        this.descriptionFunction = descriptionFunction;
        return this;
    }
}