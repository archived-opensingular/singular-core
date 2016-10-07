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

package org.opensingular.form.validation;

import org.opensingular.form.SInstance;

/**
 * Validator para {@link SInstance}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends SInstance> {
    
    void validate(IInstanceValidatable<I> validatable);
    
    /**
     * Caso este método retorne <code>true</code>, este validador só será executado caso a instância correspondente não
     * possua nenhum erro em seus descendentes. Caso retorne <code>false</code>, será executado independentemente da
     * validade de seus descendentes (campos obrigatórios poderão estar nulos neste caso).
     * @return se este validador deve ou não ser executado caso seus descendentes contenham erros. Por padrão, returna <code>true</code>. 
     */
    default boolean executeOnlyIfChildrenValid() {
        return true;
    }
}
