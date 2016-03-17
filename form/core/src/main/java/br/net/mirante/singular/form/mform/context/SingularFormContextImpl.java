/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public abstract class SingularFormContextImpl implements SingularFormContext {

    private InternalSingularFormConfig config;


    public SingularFormContextImpl(InternalSingularFormConfig config) {
        this.config = config;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return config.getServiceRegistry();
    }
}
