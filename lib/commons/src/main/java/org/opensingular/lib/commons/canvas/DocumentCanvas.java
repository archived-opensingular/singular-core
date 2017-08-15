package org.opensingular.lib.commons.canvas;

import java.util.List;

public interface DocumentCanvas {
    void addTitle(String title);

    DocumentCanvas newChild();

    void label(String label, String value);

    void breakLine();

    void list(List<String> values);
}
