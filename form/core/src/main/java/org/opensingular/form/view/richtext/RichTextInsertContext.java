package org.opensingular.form.view.richtext;

public class RichTextInsertContext implements RichTextContext {

    private String value;

    @Override
    public void setReturnValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
