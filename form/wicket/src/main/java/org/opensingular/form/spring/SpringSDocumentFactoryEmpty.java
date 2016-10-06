/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.spring;

import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.ServiceRegistry;

/**
 * Representa uma factory que não faz nada com o documento e que aponta o
 * registro ({@link #getServiceRegistry()}) para o Spring.
 *
 * @author Daniel C. Bordin
 */
public class SpringSDocumentFactoryEmpty extends SDocumentFactory {

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        return new SpringRefEmptySDocumentFactory();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return new SpringServiceRegistry(SpringFormUtil.getApplicationContext());
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class SpringRefEmptySDocumentFactory extends RefSDocumentFactory {

        @Override
        protected SDocumentFactory retrieve() {
            return new SpringSDocumentFactoryEmpty();
        }
    }
}
