/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.model;

import br.net.mirante.singular.commons.lambda.ISupplier;

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
