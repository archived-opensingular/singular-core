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
import java.io.Serializable;

public interface VarService extends Serializable {

    @Nonnull
    VarService deserialize();

    @Nonnull
    VarDefinitionMap<?> newVarDefinitionMap();

    /* TODO Verifica se ficou em uso no final, senão apagar */
    @Nonnull
    VarInstance newVarInstance(VarDefinition def);

    @Nonnull
    VarDefinition newDefinition(@Nonnull String ref, String name, @Nonnull VarType type);

    /**
     * Cria uma definição para uma classe específica. Use preferencialmente os métodos addDefinitionXXXX com um tipo ja
     * previsto.
     */
    @Nonnull
    VarDefinition newDefinitionCustom(@Nonnull String ref, String name, @Nonnull Class<?> variableClass);

    @Nonnull
    VarDefinition newDefinitionString(@Nonnull String ref, String name, Integer tamanhoMaximo);

    @Nonnull
    VarDefinition newDefinitionMultiLineString(@Nonnull String ref, String name, Integer tamanhoMaximo);

    @Nonnull
    VarDefinition newDefinitionDate(@Nonnull String ref, String name);

    @Nonnull
    VarDefinition newDefinitionInteger(@Nonnull String ref, String name);

    @Nonnull
    VarDefinition newDefinitionBoolean(@Nonnull String ref, String name);

    @Nonnull
    VarDefinition newDefinitionDouble(@Nonnull String ref, String name);

    @Nonnull
    VarDefinition newDefinitionBigDecimal(@Nonnull String ref, String name);

    static VarService basic() {
        return DefaultVarService.DEFAULT_VAR_SERVICE;
    }

    @Nonnull
    static VarService getVarService(@Nonnull VarServiceEnabled source) {
        VarService s = source.getVarService();
        if (s == null) {
            throw new UnsupportedOperationException("Falta implementar VarServiceBasic.class");
        }
        return s.deserialize();
    }
}
