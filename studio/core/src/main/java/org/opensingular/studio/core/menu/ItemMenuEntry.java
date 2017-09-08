package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public class ItemMenuEntry extends AbstractMenuEntry {
    private String endpoint;

    public ItemMenuEntry(Icon icon, String name, String endpoint) {
        super(icon, name);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}