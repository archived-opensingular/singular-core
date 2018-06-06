package org.opensingular.form.view.richtext;

import javax.annotation.Nonnull;

public class RichTextSelectionContext implements RichTextContext {

    private String textSelected;

    @Override
    public void setReturnValue(@Nonnull String value) {
        this.textSelected = value;
    }

    @Override
    public String getValue() {
        return textSelected;
    }

}
