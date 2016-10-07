/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.context;

import org.opensingular.form.document.ServiceRegistry;

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
