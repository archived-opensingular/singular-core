/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;

import br.net.mirante.singular.util.wicket.resource.Icone;

public class ItemAction implements Serializable {

    private String name;
    private boolean defaultAction;

    private String label;
    private Icone icon;
    private ItemActionType type;

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
}
