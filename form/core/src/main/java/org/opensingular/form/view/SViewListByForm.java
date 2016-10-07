/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.SType;

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
