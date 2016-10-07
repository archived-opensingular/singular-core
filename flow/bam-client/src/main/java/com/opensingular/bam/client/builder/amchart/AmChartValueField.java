/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;

import java.io.Serializable;

public class AmChartValueField implements Serializable {

    private String propertyName;
    private String title;
    private String suffix;

    public AmChartValueField() {
    }

    public AmChartValueField(String propertyName, String title) {
        this(propertyName, title, "");
    }

    public AmChartValueField(String propertyName, String title, String suffix) {
        this.propertyName = propertyName;
        this.title = title;
        this.suffix = suffix;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getTitle() {
        return title;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
