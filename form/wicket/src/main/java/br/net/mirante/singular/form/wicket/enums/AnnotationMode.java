/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.enums;

public enum AnnotationMode {

    NONE, EDIT, READ_ONLY;

    public boolean editable() {
        return this.equals(EDIT);
    }

    public boolean enabled() {
        return !this.equals(NONE);
    }
}
