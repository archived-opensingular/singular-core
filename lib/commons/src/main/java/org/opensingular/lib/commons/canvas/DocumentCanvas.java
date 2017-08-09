package org.opensingular.lib.commons.canvas;

public interface DocumentCanvas {
    void addTitle(String title);

    DocumentCanvas newChild();

    void label(String label, String value);

    void breakLine();
}
