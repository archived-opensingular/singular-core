package org.opensingular.form.view.richtext;

public class RichTextSelectionContext implements RichTextContext {

    private String textSelected;
    private String newValue;

    public RichTextSelectionContext(String textSelected) {
        this.textSelected = textSelected;
    }

    @Override
    public void setReturnValue(String value) {
        this.newValue = value;
    }

    @Override
    public String getValue() {
        return newValue;
    }

    public String getTextSelected() {
        return this.textSelected;
    }

}
