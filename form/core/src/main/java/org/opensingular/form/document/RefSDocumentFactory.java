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

package org.opensingular.form.document;

import org.opensingular.form.internal.util.SerializableReference;

/**
 * É uma referência a uma {@link SDocumentFactory} que pode ser serializada com
 * segurança e posteriormente (depois de deserializado) será capaz de localizar
 * a refência à factory, que em geral não é serializável. Particularmente útil
 * na integração do formulário com interfaces web.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefSDocumentFactory extends SerializableReference<SDocumentFactory> {

    public RefSDocumentFactory() {
    }

    public RefSDocumentFactory(SDocumentFactory documentFactory) {
        super(documentFactory);
    }
}
