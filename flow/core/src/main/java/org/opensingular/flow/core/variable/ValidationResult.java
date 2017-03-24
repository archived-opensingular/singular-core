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

package org.opensingular.flow.core.variable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa o resultado de uma validação.
 *
 * @author Daniel C. Bordin
 */
public class ValidationResult {

    private List<String> errors;

    public boolean hasErros() {
        return errors != null && !errors.isEmpty();
    }

    public void addErro(String msg) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(msg);
    }

    public void addErro(VarInstance var, String msg) {
        addErro(var.getName() + ": " + msg);
    }

    @Override
    public String toString() {
        if (!hasErros()) {
            return "[]";
        }
        return errors.stream().collect(Collectors.joining("\n"));
    }

    /** Retorna uma lista dos erros encontrados. */
    @Nonnull
    public List<String> errors() {
        return errors == null ? Collections.emptyList() : errors;
    }

}
