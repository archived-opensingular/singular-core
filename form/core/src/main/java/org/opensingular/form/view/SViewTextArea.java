/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

public class SViewTextArea extends SView {

    private Integer lines = 3;

    public SViewTextArea() {
    }

    public Integer getLines() {
        return lines;
    }

    public SViewTextArea setLines(Integer lines) {
        this.lines = lines;
        return this;
    }
}
