package org.opensingular.form.view;

import org.opensingular.lib.commons.ui.Alignment;

/**
 * SView for configure the CheckBox.
 */
public class SViewCheckBox extends SView {

    /**
     * Variable for configure the alignment of the Label of checkbox.
     * It's useful for <code> SViewListByTable </code>.
     */
    private Alignment alignmentLabel;

    /**
     * Method for change de alignment label of checkbox.
     * <p>
     * If the alignment is configurated,  the label above the checkbox.
     * <p>
     * <code>CENTER</code> will put the text-align:center.
     * <code>LEFT</code> will put the text-align:left.
     * <code>RIGHT</code> will put the text-align:right.
     *
     * @param alignment The alignment of label.
     * @return <code>this</code>
     */
    public SViewCheckBox setAlignLabelOfCheckBox(Alignment alignment) {
        this.alignmentLabel = alignment;
        return this;
    }

    /**
     * Returns the alignment of label.
     * If the alignment was not null, the Label will be put above the checkbox.
     *
     * @return The alignment.
     */
    public Alignment getAlignmentOfLabel() {
        return alignmentLabel;
    }
}
