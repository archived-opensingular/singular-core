package org.opensingular.form.view.richtext;

import javax.annotation.Nonnull;

public class RichTextSelectionContext implements RichTextContext {

    private String textSelected;
    private String value;

    @Override
    public void setReturnValue(@Nonnull String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getTextSelected() {
        return textSelected;
    }
}
