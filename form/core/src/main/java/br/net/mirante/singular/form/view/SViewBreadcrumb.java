/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;

public class SViewBreadcrumb extends AbstractSViewListWithCustomColuns<SViewBreadcrumb> {

    private boolean editEnabled = true;
    private boolean showTable = true;

    public SViewBreadcrumb disableEdit() {
        this.editEnabled = false;
        return this;
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }

    public boolean isShowTable() {
        return showTable;
    }

    public void setShowTable(boolean showTable) {
        this.showTable = showTable;
    }
}
