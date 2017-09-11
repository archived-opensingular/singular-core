package org.opensingular.lib.commons.canvas;

public class GridItem {
    private final String value;
    private final Integer cols;

    public GridItem(String value, Integer cols) {
        this.value = value;
        this.cols = cols;
    }

    public Integer getCols() {
        return cols;
    }

    public String getValue() {
        return value;
    }
}
