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

/**
 * Representa uma factory que não faz nada com o documento.
 *
 * @author Daniel C. Bordin
 */
final class SDocumentFactoryEmpty extends SDocumentFactory {

    private static SDocumentFactoryEmpty instance;

    private SDocumentFactoryEmpty() {}

    synchronized static SDocumentFactory getEmptyInstance() {
        if (instance == null) {
            instance = new SDocumentFactoryEmpty();
        }
        return instance;
    }

    @Override
    protected RefSDocumentFactory createDocumentFactoryRef() {
        return new RefEmptySDocumentFactory(this);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return null;
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class RefEmptySDocumentFactory extends RefSDocumentFactory {

        public RefEmptySDocumentFactory(SDocumentFactoryEmpty factory) {
            super(factory);
        }

        @Override
        protected SDocumentFactory retrieve() {
            return getEmptyInstance();
        }
    }
}
