package org.opensingular.form.view.richtext;

import java.io.Serializable;

public class CkEditorContext implements Serializable {

    private String value;
    private String valueSelected;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueSelected() {
        return valueSelected;
    }

    public void setValueSelected(String valueSelected) {
        this.valueSelected = valueSelected;
    }
}
