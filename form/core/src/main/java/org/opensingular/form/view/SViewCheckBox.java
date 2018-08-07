/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
