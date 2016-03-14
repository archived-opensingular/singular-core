/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

import br.net.mirante.singular.bamclient.portlet.filter.FieldType;
import br.net.mirante.singular.bamclient.portlet.filter.RestReturnType;

public class FilterConfig implements Serializable {

    private String identifier;
    private FieldType fieldType;
    private String label;
    private Integer size;
    private String[] options;
    private String restEndpoint;
    private RestReturnType restReturnType;
    private Boolean required = Boolean.FALSE;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    public void setRestEndpoint(String restEndpoint) {
        this.restEndpoint = restEndpoint;
    }

    public RestReturnType getRestReturnType() {
        return restReturnType;
    }

    public void setRestReturnType(RestReturnType restReturnType) {
        this.restReturnType = restReturnType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}