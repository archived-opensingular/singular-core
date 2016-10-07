/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;

public class ProcessDTO implements Serializable {

    private String name;
    private String formName;
    private String abbreviation;

    public ProcessDTO() {
    }

    public ProcessDTO(String abbreviation, String name, String formName) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.formName = formName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormName() {
        return formName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

}