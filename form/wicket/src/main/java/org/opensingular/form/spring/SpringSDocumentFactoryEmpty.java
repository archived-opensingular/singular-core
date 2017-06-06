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

package org.opensingular.form.spring;

import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;

import javax.annotation.Nonnull;

/**
 * Representa uma factory que não faz nada com o documento e que aponta o
 *
 * @author Daniel C. Bordin
 */
public class SpringSDocumentFactoryEmpty extends SDocumentFactory {

    private SpringServiceRegistry registry;

    @Override
    protected RefSDocumentFactory createDocumentFactoryRef() {
        return new SpringRefEmptySDocumentFactory(this);
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class SpringRefEmptySDocumentFactory extends RefSDocumentFactory {

        public SpringRefEmptySDocumentFactory(SpringSDocumentFactoryEmpty factory) {
            super(factory);
        }

        @Override
        protected SDocumentFactory retrieve() {
            return new SpringSDocumentFactoryEmpty();
        }
    }
}
