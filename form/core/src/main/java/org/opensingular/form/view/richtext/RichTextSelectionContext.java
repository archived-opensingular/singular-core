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
