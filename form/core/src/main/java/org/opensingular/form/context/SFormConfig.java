/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.context;

import org.opensingular.form.document.TypeLoader;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.ServiceRegistry;

import java.io.Serializable;

/**
 * Representa a configuração para funcionamento do formulário em termo de
 * recuperação e montagem (setup inicial).
 *
 * @author Daniel C. Bordin
 */
public interface SFormConfig<TYPE_KEY extends Serializable> {

    /** Devolve o configurador para o setup inicia do documento. */
    public SDocumentFactory getDocumentFactory();

    /** Devolve o carregador de tipo. */
    public TypeLoader<TYPE_KEY> getTypeLoader();

    /** Devolve o registro de recursos adicionais. */
    public default ServiceRegistry getServiceRegistry() {
        return getDocumentFactory().getServiceRegistry();
    }
}
