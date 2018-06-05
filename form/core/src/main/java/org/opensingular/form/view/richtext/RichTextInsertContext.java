package org.opensingular.form.view.richtext;

import javax.annotation.Nonnull;

public class RichTextInsertContext implements RichTextContext {

    private String value;

    @Override
    public void setReturnValue(@Nonnull String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
