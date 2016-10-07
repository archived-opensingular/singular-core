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

package org.opensingular.form.processor;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SType;

/**
 * Processador de tipo chamado assim que um novo tipo é adicionado ao dicionário, ou seja, assim que torna-se um tipo
 * efetivamente. Suas implementações deve ser registradas no dicionário ou no pacote para garantir a execução.
 *
 * @author Daniel C. Bordin
 */
public interface TypeProcessorPosRegister {

    /**
     * @param type         Tipo que foi carregado
     * @param onLoadCalled Indica se o tipo teve o método {@link SType#onLoadType(TypeBuilder)} chamado para o tipo ou
     *                     não (se for a extensão de um tipo que já teve o método chamado).
     */
    public void processTypePosRegister(SType<?> type, boolean onLoadCalled);
}
