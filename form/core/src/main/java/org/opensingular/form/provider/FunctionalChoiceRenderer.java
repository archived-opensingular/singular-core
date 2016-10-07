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

import org.opensingular.lib.commons.lambda.IFunction;

import java.io.Serializable;

public class FunctionalChoiceRenderer<T extends Serializable> implements ChoiceRenderer<T> {

    private static final long serialVersionUID = 5161774886991801853L;

    private IFunction<T, String> displayFunction;
    private IFunction<T, String> idFunction;

    @Override
    public String getIdValue(T option) {
        return idFunction.apply(option);
    }

    @Override
    public String getDisplayValue(T option) {
        return displayFunction.apply(option);
    }

    public IFunction<T, String> getDisplayFunction() {
        return displayFunction;
    }

    public void setDisplayFunction(IFunction<T, String> displayFunction) {
        this.displayFunction = displayFunction;
    }

    public IFunction<T, String> getIdFunction() {
        return idFunction;
    }

    public void setIdFunction(IFunction<T, String> idFunction) {
        this.idFunction = idFunction;
    }

}