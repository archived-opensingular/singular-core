package org.opensingular.form.view.richtext;

/**
 * This class respresent a stylized button for change all Content of the RichText.
 */
public class RichTextContentContext implements RichTextContext {

    /**
     * The current value of content.
     */
    private String content;

    /**
     * The new value of content.
     */
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
