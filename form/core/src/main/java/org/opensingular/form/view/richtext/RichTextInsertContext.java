package org.opensingular.form.view.richtext;

/**
 * This class respresent a stylized button for just insert new text in the RichText.
 */
public class RichTextInsertContext implements RichTextContext {

    /**
     * The value that will be insert.
     */
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
