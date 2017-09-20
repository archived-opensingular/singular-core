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

package org.opensingular.form.view;

import java.util.Optional;

import org.opensingular.lib.commons.lambda.IFunction;

public class SViewBooleanSwitch<T> extends SView {

    private IFunction<T, String> textFunction;
    private IFunction<T, String> colorFunction;

    public Optional<String> getText(T value) {
        return (textFunction != null)
            ? Optional.ofNullable(textFunction.apply(value))
            : Optional.empty();
    }

    public Optional<String> getColor(T value) {
        return (colorFunction != null)
            ? Optional.ofNullable(colorFunction.apply(value))
            : Optional.empty();
    }

    //@formatter:off
    public IFunction<T, String> getTextFunction()  { return textFunction; }
    public IFunction<T, String> getColorFunction() { return colorFunction; }
    public SViewBooleanSwitch<T> setTextFunction (IFunction<T, String>  textFunction) { this.textFunction  = textFunction; return this; }
    public SViewBooleanSwitch<T> setColorFunction(IFunction<T, String> colorFunction) { this.colorFunction = colorFunction; return this; }
    //@formatter:on
}
