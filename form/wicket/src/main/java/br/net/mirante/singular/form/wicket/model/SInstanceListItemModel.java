/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.SInstance;

public class SInstanceListItemModel<I extends SInstance>
    extends AbstractSInstanceItemListaModel<I>
{

    private int index;

    public SInstanceListItemModel(Object rootTarget, int index) {
        super(rootTarget);
        this.index = index;
    }

    @Override
    protected int index() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
