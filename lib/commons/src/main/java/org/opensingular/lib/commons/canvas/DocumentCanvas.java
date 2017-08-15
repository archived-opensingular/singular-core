package org.opensingular.lib.commons.canvas;

import java.util.List;

public interface DocumentCanvas {
    void addTitle(String title);

    DocumentCanvas newChild();

    void label(FormItem formItem);

    void breakLine();

    void list(List<String> values);

}
