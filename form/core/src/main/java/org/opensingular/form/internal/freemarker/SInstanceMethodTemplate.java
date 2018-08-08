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

package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateMethodModelEx;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.util.List;

public abstract class SInstanceMethodTemplate<INSTANCE extends SInstance> implements TemplateMethodModelEx {
    private final INSTANCE instance;
    private final String methodName;

    public SInstanceMethodTemplate(INSTANCE instance, String methodName) {
        this.instance = instance;
        this.methodName = methodName;
    }

    protected INSTANCE getInstance() {
        return instance;
    }

    protected void checkNumberOfArguments(List<?> arguments, int expected) {
        if (expected != arguments.size()) {
            throw new SingularFormException("A chamada do método '" + methodName + "'() em " + getInstance().getPathFull()
                    + "deveria ter " + expected + " argumentos, mas foi feito com " + arguments + " argumentos.");
        }
    }
}