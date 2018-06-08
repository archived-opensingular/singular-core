package org.opensingular.form.view.richtext;

/**
 * This class respresent a stylized button for change just the text Selected of the RichText.
 */
public class RichTextSelectionContext implements RichTextContext {

    /**
     * The text selected.
     */
    private String textSelected;

    /**
     * The new value of the selected text.
     */
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
