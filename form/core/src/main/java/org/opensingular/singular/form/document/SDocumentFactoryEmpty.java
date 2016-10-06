/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.document;

/**
 * Representa uma factory que não faz nada com o documento.
 *
 * @author Daniel C. Bordin
 */
public class SDocumentFactoryEmpty extends SDocumentFactory {

    private static SDocumentFactoryEmpty instance;

    protected SDocumentFactoryEmpty() {}

    final static SDocumentFactory getEmptyInstance() {
        if (instance == null) {
            synchronized (SDocumentFactoryEmpty.class) {
                if (instance == null) {
                    instance = new SDocumentFactoryEmpty();
                }
            }
        }
        return instance;
    }

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        return new RefEmptySDocumentFactory();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return null;
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class RefEmptySDocumentFactory extends RefSDocumentFactory {

        @Override
        protected SDocumentFactory retrieve() {
            return getEmptyInstance();
        }
    }
}
