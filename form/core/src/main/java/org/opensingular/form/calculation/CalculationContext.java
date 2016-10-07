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

package org.opensingular.form.calculation;

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.util.Objects;

public class CalculationContext {

    private SInstance instanceContext;

    public CalculationContext(SInstance instanceContext) {
        this.instanceContext = Objects.requireNonNull(instanceContext);
    }

    public SInstance instance() {
        if (instanceContext == null) {
            throw new SingularFormException("Esse contexto não é baseado em instância");
        }
        return instanceContext;
    }
}
