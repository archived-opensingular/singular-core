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
 * Representa configurações globais do dicionário.
 *
 * @author Daniel C. Bordin
 */
public class SDictionaryConfig {

    private final SDictionary dictionary;

    public SDictionaryConfig(SDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public SDictionary getDictionary() {
        return dictionary;
    }

    //TODO (por Daniel Bordin - 29/05/16) Acabou ficando sem conteudo, senão for usado até o fim do ano, apagar
}
