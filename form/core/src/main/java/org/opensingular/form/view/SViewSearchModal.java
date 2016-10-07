/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class SViewSearchModal extends SView {

    private String       title    = StringUtils.EMPTY;
    private Integer      pageSize = 5;

    public SViewSearchModal() {
    }

    public SViewSearchModal title(String title) {
        this.title = title;
        return this;
    }

    public SViewSearchModal withPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPageSize() {
        return pageSize;
    }

}

