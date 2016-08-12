/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ProcessDTO implements Serializable {

    private String       name;
    private String       formName;
    private List<String> abbreviations;

    public ProcessDTO() {
    }

    public ProcessDTO(List<String> abbreviations, String name, String formName) {
        this.abbreviations = abbreviations;
        this.name = name;
        this.formName = formName;
    }

    public ProcessDTO(String abbreviation, String name, String formName) {
        this(Collections.singletonList(abbreviation), name, formName);
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

    public List<String> getAbbreviations() {
        return abbreviations;
    }

}