/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.enums;

public enum AnnotationMode {

    NONE, EDIT, READ_ONLY;

    public boolean editable() {
        return this.equals(EDIT);
    }

    public boolean enabled() {
        return !this.equals(NONE);
    }
}
