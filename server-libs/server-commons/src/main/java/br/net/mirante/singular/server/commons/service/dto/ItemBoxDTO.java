/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;
import java.util.Map;

public class ItemBoxDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private boolean newButton = false;
    private boolean editButton = false;
    private boolean deleteButton = false;
    private boolean viewButton = false;
    private boolean quickFilter = true;
    private String searchEndpoint;
    private String countEndpoint;
    private Map<String, String> fieldsDatatable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNewButton() {
        return newButton;
    }

    public void setNewButton(boolean newButton) {
        this.newButton = newButton;
    }

    public boolean isEditButton() {
        return editButton;
    }

    public void setEditButton(boolean editButton) {
        this.editButton = editButton;
    }

    public boolean isDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(boolean deleteButton) {
        this.deleteButton = deleteButton;
    }

    public String getSearchEndpoint() {
        return searchEndpoint;
    }

    public void setSearchEndpoint(String searchEndpoint) {
        this.searchEndpoint = searchEndpoint;
    }

    public boolean isQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(boolean quickFilter) {
        this.quickFilter = quickFilter;
    }

    public Map<String, String> getFieldsDatatable() {
        return fieldsDatatable;
    }

    public void setFieldsDatatable(Map<String, String> fieldsDatatable) {
        this.fieldsDatatable = fieldsDatatable;
    }

    public boolean isViewButton() {
        return viewButton;
    }

    public void setViewButton(boolean viewButton) {
        this.viewButton = viewButton;
    }

    public String getCountEndpoint() {
        return countEndpoint;
    }

    public void setCountEndpoint(String countEndpoint) {
        this.countEndpoint = countEndpoint;
    }
}
