/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.opensingular.lib.wicket.util.resource.Icone;

public class ItemBox implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private boolean showNewButton = false;
    private boolean quickFilter = true;
    private boolean showDraft = false;
    private Boolean endedTasks;
    private String searchEndpoint;
    private String countEndpoint;
    private Icone icone;
    private LinkedHashMap<String, String> fieldsDatatable;

    // Ações disponíveis para todos os processos
    private LinkedHashMap<String, ItemAction> actions;

    // Ações específicas para um processo
    private LinkedHashMap<String, ItemAction> processActions;

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

    public boolean isShowNewButton() {
        return showNewButton;
    }

    public void setShowNewButton(boolean showNewButton) {
        this.showNewButton = showNewButton;
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

    public LinkedHashMap<String, String> getFieldsDatatable() {
        return fieldsDatatable;
    }

    public void setFieldsDatatable(LinkedHashMap<String, String> fieldsDatatable) {
        this.fieldsDatatable = fieldsDatatable;
    }

    public String getCountEndpoint() {
        return countEndpoint;
    }

    public void setCountEndpoint(String countEndpoint) {
        this.countEndpoint = countEndpoint;
    }

    public boolean isShowDraft() {
        return showDraft;
    }

    public void setShowDraft(boolean showDraft) {
        this.showDraft = showDraft;
    }

    public LinkedHashMap<String, ItemAction> getActions() {
        if (actions == null) {
            actions = new LinkedHashMap<>();
        }
        return actions;
    }

    public void setActions(LinkedHashMap<String, ItemAction> actions) {
        this.actions = actions;
    }

    public ItemBox addAction(ItemAction itemAction) {
        getActions().put(itemAction.getName(), itemAction);
        return this;
    }

    public LinkedHashMap<String, ItemAction> getProcessActions() {
        if (processActions == null) {
            processActions = new LinkedHashMap<>();
        }
        return processActions;
    }

    public void setProcessActions(LinkedHashMap<String, ItemAction> processActions) {
        this.processActions = processActions;
    }

    public ItemBox addProcessAction(ProcessDTO processDTO, ItemAction itemAction) {
        getProcessActions().put(processDTO.getName(), itemAction);
        return this;
    }

    public Icone getIcone() {
        return icone;
    }

    public void setIcone(Icone icone) {
        this.icone = icone;
    }

    public Boolean getEndedTasks() {
        return endedTasks;
    }

    public void setEndedTasks(Boolean endedTasks) {
        this.endedTasks = endedTasks;
    }
}
