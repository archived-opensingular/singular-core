/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.enums;


public enum ViewMode {

    EDITION,
    VISUALIZATION;

    public boolean isEdition() {
        return this.equals(EDITION);
    }

    public boolean isVisualization() {
        return this.equals(VISUALIZATION);
    }
}
