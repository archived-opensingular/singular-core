package org.opensingular.form.view.richtext;

import javax.annotation.Nonnull;

public class RichTextContentContext implements RichTextContext {


    private String content;
    private String value;

    @Override
    public void setReturnValue(@Nonnull String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getContent() {
        return content;
    }
}
