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

package org.opensingular.form.spring;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistenceInMemory;

import javax.inject.Inject;

/**
 * @param <TYPE>
 * @param <INSTANCE>
 * @author ronaldtm
 */
public class SpringFormPersistenceInMemory<TYPE extends SType<INSTANCE>, INSTANCE extends SInstance>
        extends FormPersistenceInMemory<TYPE, INSTANCE> {
    private final Class<? extends SType<?>> type;

    private SDocumentFactory documentFactory;

    public SpringFormPersistenceInMemory(Class<? extends SType<?>> type) {
        this.type = type;
    }

    @Override
    public INSTANCE createInstance() {
        return (INSTANCE) documentFactory.createInstance(RefType.of(type));
    }

    @Inject
    public void setDocumentFactory(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }
}