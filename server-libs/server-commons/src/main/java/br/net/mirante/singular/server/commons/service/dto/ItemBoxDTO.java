/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;

public class ItemBoxDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private boolean newButton = false;
    private boolean editButton = false;
    private boolean deleteButton = false;
    private String searchEndpoint;

    public String getName() {
        return nome;
    }

    public void setName(String name) {
        this.nome = name;
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
}
