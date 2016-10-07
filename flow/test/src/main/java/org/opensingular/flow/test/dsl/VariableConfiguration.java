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

package org.opensingular.flow.test.dsl;

import org.opensingular.flow.core.variable.VarType;

public class VariableConfiguration {

    public  VariableConfiguration addVariable(String ref, VarType tipo) {
        return this;
    }

    public  VariableConfiguration addVariableBoolean(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableDouble(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableInteger(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableInteger(String ref) {
        return this;
    }

    public  VariableConfiguration addVariableDate(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableDate(String ref) {
        return this;
    }

    public  VariableConfiguration addVariableStringMultipleLines(String ref, String name, Integer tamanhoMaximo) {
        return this;
    }

    public  VariableConfiguration addVariableStringMultipleLines(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref, String name, Integer tamanhoMaximo) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref) {
        return addVariableString(ref, ref, null);
    }
}
