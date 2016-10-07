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

package org.opensingular.form;

/**
 * Classe de suporte a construção de um tipo durante a chamada do método {@link SType#onLoadType(TypeBuilder)}.
 */
public class TypeBuilder {
    //TODO (por Daniel Bordin 29/05/2016) Por em quanto não é muito útil essa classe. Verificar a permanência dela se
    // não encontrarmos utilidade até o fim do ano

    private final SType<?> targetType;

    <X extends SType<?>> TypeBuilder(X newType) {
        this.targetType = newType;
    }

    final SType<?> getType() {
        return targetType;
    }
}
