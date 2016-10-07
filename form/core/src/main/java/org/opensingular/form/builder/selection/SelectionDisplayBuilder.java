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
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.SType;
import org.opensingular.form.provider.FreemarkerUtil;

import java.io.Serializable;

public class SelectionDisplayBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public SelectionDisplayBuilder(SType<?> type) {
        super(type);
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> selfDisplay() {
        type.asAtrProvider().displayFunction((o) -> o);
        return next();
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> display(String freemarkerTemplate) {
        type.asAtrProvider().displayFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> display(IFunction<TYPE, String> valor) {
        type.asAtrProvider().displayFunction(valor);
        return next();
    }

    private ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> next() {
        return new ConverterBuilder<>(type);
    }
}
