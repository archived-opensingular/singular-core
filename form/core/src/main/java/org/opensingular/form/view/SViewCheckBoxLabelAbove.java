package org.opensingular.form.view;

import org.opensingular.lib.commons.ui.Alignment;

/**
 * SView for put the Label Above the CheckBox.
 * It's useful for <code> SViewListByTable </code>.
 */
public class SViewCheckBoxLabelAbove extends SView {

    /**
     * Variable for alignment of the checkbox
     */
    private Alignment alignment;

    /**
     * Method for change de alignment of checkbox.
     * <code>CENTER</code> will put the text-align:center.
     * <code>LEFT</code> will put the text-align:left.
     * <code>RIGHT</code> will put the text-align:right.
     *
     * @param alignment The alignment.
     * @return <code>this</code>
     */
    public SViewCheckBoxLabelAbove setAlignCheckBox(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Returns the alignment.
     * @return The alignment.
     */
    public Alignment getAlignment() {
        return alignment;
    }
}
