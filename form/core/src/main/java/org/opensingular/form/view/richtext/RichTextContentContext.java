package org.opensingular.form.view.richtext;

public class RichTextContentContext implements RichTextContext {


    private String content = "";
    private String newValue;

    public RichTextContentContext(String content) {
        this.content = content;
    }

    @Override
    public void setReturnValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String getValue() {
        return this.newValue;
    }

    public String getContent() {
        return this.content;
    }
}
