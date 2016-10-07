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

import org.opensingular.form.internal.util.SerializableReference;

/**
 * Representa uma referência serializável a um dicionário. Deve ser derivado de
 * modo que ao ser deserializado seja capaz de recuperar ou recontruir o
 * dicionário. OS métodos mais comuns seria recriar o dicionário do zero ou
 * recuperar de algum cache estátivo em memória.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefSDictionary extends SerializableReference<SDictionary> {

    public RefSDictionary() {
    }

    public RefSDictionary(SDictionary dictionary) {
        super(dictionary);
    }
}
