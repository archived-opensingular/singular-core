/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

public class SViewListByMasterDetail extends AbstractSViewListWithCustomColuns<SViewListByMasterDetail> {

    private boolean editEnabled = true;

    public SViewListByMasterDetail disableEdit() {
        this.editEnabled = false;
        return this;
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }
}
