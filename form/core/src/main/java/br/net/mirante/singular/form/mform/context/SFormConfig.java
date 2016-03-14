/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.context;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.mform.document.TypeLoader;

/**
 * Representa a configuração para funcionamento do formulário em termo de
 * recuperação e montagem (setup inicial).
 *
 * @author Daniel C. Bordin
 */
public interface SFormConfig<KEY extends Serializable> {

    /** Devolve o configurador para o setup inicia do documento. */
    public SDocumentFactory getDocumentFactory();

    /** Devolve o carregador de tipo. */
    public TypeLoader<KEY> getTypeLoader();

    /** Devolve o registro de recursos adicionais. */
    public default ServiceRegistry getServiceRegistry() {
        return getDocumentFactory().getServiceRegistry();
    }
}
