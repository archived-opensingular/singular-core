/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModelException;
import org.opensingular.form.SInstance;

import java.util.List;
import java.util.function.Function;

public class SInstanceZeroArgumentMethodTemplate<INSTANCE extends SInstance> extends SInstanceMethodTemplate<INSTANCE> {

    private final Function<INSTANCE, Object> function;

    public SInstanceZeroArgumentMethodTemplate(INSTANCE instance, String methodName, Function<INSTANCE, Object> function) {
        super(instance, methodName);
        this.function = function;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        checkNumberOfArguments(arguments, 0);
        return function.apply(getInstance());
    }
}