/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.spring;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.RefSDocumentFactory;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
