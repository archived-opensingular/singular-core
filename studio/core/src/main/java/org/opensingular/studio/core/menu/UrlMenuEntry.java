package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public class UrlMenuEntry extends ItemMenuEntry {

    private final String endpoint;

    public UrlMenuEntry(Icon icon, String name, String endpoint) {
        super(icon, name);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

}