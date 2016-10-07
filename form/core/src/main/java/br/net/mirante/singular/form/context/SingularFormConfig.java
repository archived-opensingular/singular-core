/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.context;

import br.net.mirante.singular.form.document.ServiceRegistry;

public interface SingularFormConfig {


    public void setServiceRegistry(ServiceRegistry serviceRegistry);

    /**
     * Método factory para criar novo contexto de montagem ou manipulação de
     * formulário.
     */
    public SingularFormContext createContext();

}
