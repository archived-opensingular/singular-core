/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.enums;


public enum ViewMode {

    EDIT,
    READ_ONLY;

    public boolean isEdition() {
        return this.equals(EDIT);
    }

    public boolean isVisualization() {
        return this.equals(READ_ONLY);
    }
}
