/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;

import org.opensingular.lib.wicket.util.resource.Icone;

public class ItemAction implements Serializable {

    private String name;
    private boolean defaultAction;

    private String label;
    private Icone icon;
    private ItemActionType type;

    private ItemActionConfirmation confirmation;

    public ItemAction() {
    }

    public ItemAction(String name) {
        this.name = name;
        defaultAction = true;
    }

    public ItemAction(String name, String label, Icone icon, ItemActionType type) {
        this.name = name;
        this.label = label;
        this.icon = icon;
        this.type = type;
        defaultAction = false;
    }

    public ItemAction(String name, String label, Icone icon, ItemActionType type, ItemActionConfirmation confirmation) {
        this.name = name;
        this.label = label;
        this.icon = icon;
        this.type = type;
        this.confirmation = confirmation;
        defaultAction = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
    }

    public ItemActionType getType() {
        return type;
    }

    public void setType(ItemActionType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Icone getIcon() {
        return icon;
    }

    public void setIcon(Icone icon) {
        this.icon = icon;
    }

    public ItemActionConfirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(ItemActionConfirmation confirmation) {
        this.confirmation = confirmation;
    }
}
