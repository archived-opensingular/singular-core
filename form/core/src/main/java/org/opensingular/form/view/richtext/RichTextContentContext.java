package org.opensingular.form.view.richtext;

import javax.annotation.Nonnull;

public class RichTextContentContext implements RichTextContext {


    private String content;

    @Override
    public void setReturnValue(@Nonnull String value) {
        this.content = value;
    }

    @Override
    public String getValue() {
        return content;
    }
}
