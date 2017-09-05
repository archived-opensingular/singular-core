package org.opensingular.lib.commons.canvas;

public class FormItem {
    private String label;
    private String value;
    private Integer cols;

    public FormItem(String label, String value, Integer cols) {
        this.label = label;
        this.value = value;
        this.cols = cols;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public Integer getCols() {
        return cols;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public boolean isValueAndLabelNull(){
        return value == null && label == null;
    }
}
