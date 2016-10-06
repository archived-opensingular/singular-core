/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.model;

import org.opensingular.lib.commons.lambda.ISupplier;

@SuppressWarnings("serial")
public class SupplierReloadableDetachableModel<T> extends ReloadableDetachableModel<T> {
    
    private final ISupplier<T> supplier;
    
    public SupplierReloadableDetachableModel(ISupplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T load() {
        return supplier.get();
    }

}
