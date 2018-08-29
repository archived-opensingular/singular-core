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

package org.opensingular.form.view.richtext;

import java.util.Optional;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.ui.Icon;

/**
 * A interface that will be placed in the SViewByRichTextNewTab to add new configurable buttons in RichText.
 * @see SViewByRichTextNewTab SView who contains a list of buttons.
 * @param <T>
 */
public interface RichTextAction<T extends RichTextContext> {

    /**
     * The label and placeholder of the button.
     *
     * @return Label.
     */
    String getLabel();

    /**
     * Boolean that represents if will have a label inline.
     * True will show the label, False will not.
     *
     * @return True will show the label, False will not.
     */
    default boolean getLabelInline() {
        return true;
    }

    /**
     * The icon of the button, it's possible to create a new icon with css and use it.
     *
     * @return The icon css of the button.
     */
    Icon getIcon();

    /**
     * The Form of modal.
     *
     * @return The SType will present when modal is show.
     */
    Optional<Class<? extends SType<?>>> getForm();

    /**
     * The Type of The button, can be the same of <T>
     * @return The Type of button.
     */
    Class<? extends T> getType();

    /**
     * The action when is clicked on confirm button of Modal, or just the button.
     * @param richTextActionContext The RichTextContext of any subclass who contains the content of RichText.
     * @param sInstance An optional contained the SIntance of the form of modal.
     */
    void onAction(T richTextActionContext, Optional<SInstance> sInstance);
}
