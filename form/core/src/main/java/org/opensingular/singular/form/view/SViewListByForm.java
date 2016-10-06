/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.view;

import org.opensingular.singular.form.SType;

public class SViewListByForm extends AbstractSViewListWithControls<SViewListByForm> {

    private String headerPath;

    public SViewListByForm() {
    }

    public SViewListByForm(SType header) {
        this.headerPath = header.getNameSimple();
    }

    public String getHeaderPath() {
        return headerPath;
    }
}
