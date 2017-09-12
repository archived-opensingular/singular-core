package org.opensingular.studio.core.menu;

import java.io.Serializable;

public interface MenuView extends Serializable {
    String getEndpoint(String menuPath);
}