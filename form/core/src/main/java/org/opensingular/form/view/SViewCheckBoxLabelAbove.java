package org.opensingular.form.view;

import org.opensingular.lib.commons.table.Column;

public class SViewCheckBoxLabelAbove extends SView {

    private Column.Alignment alignment;

    public SViewCheckBoxLabelAbove setAlignCheckBox(Column.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public Column.Alignment getAlignment() {
        return alignment;
    }
}
