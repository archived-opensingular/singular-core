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

import org.opensingular.lib.commons.lambda.IConsumer;

import java.util.Objects;

/**
 *
 * @author Daniel C. Bordin
 */
final class SDocumentExtended extends SDocumentFactory {

    private final SDocumentFactory original;
    private final IConsumer<SDocument> extraSetupStep;
    private RefSDocumentFactoryExtended ref;

    public SDocumentExtended(SDocumentFactory original, IConsumer<SDocument> extraSetupStep) {
        this.original = Objects.requireNonNull(original);
        this.extraSetupStep = Objects.requireNonNull(extraSetupStep);
    }

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        if (ref == null) {
            ref = new RefSDocumentFactoryExtended(this);
        }
        return ref;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return original.getServiceRegistry();
    }

    @Override
    protected void setupDocument(SDocument document) {
        original.setupDocument(document);
        extraSetupStep.accept(document);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "( extend  factory " + original + ")";
    }

    /**
     * Referência serializável para a {@link RefSDocumentFactoryExtended}
     */
    private static final class RefSDocumentFactoryExtended extends RefSDocumentFactory {

        private final RefSDocumentFactory refOriginalFactory;
        private final IConsumer<SDocument> extraSetupStep;

        public RefSDocumentFactoryExtended(SDocumentExtended documentFactory) {
            super(documentFactory);
            this.refOriginalFactory = documentFactory.original.getDocumentFactoryRef();
            this.extraSetupStep = documentFactory.extraSetupStep;
        }

        @Override
        protected SDocumentFactory retrieve() {
            SDocumentFactory original = refOriginalFactory.get();
            return new SDocumentExtended(original, extraSetupStep);
        }
    }
}
