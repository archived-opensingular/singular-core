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

import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.view.SView;

/**
 * SView of RichText for NewTab, this is the Default view.
 */
public class SViewByRichTextNewTab extends SView {

    private boolean showSaveButton;
    /**
     * Class that represent some configuration of the RichText.
     */
    private RichTextConfiguration richTextConfiguration = new RichTextConfiguration(this);

    /**
     * The list of Action buttons.
     */
    private List<RichTextAction> richTextActionList = new ArrayList<>(0);

    /**
     * Method to configure the visible of Save Button.
     * Required to be in the Enabled mode to works.
     *
     * @param showSaveButton True will show the save and the closed button.
     *                       False will show the confirm and cancel button.
     * @return This view.
     */
    public SViewByRichTextNewTab showSaveButton(boolean showSaveButton) {
        this.showSaveButton = showSaveButton;
        return this;
    }

    public void addAction(RichTextAction btnRichText) {
        this.richTextActionList.add(btnRichText);
    }

    public List<RichTextAction> getTextActionList() {
        return richTextActionList;
    }

    public RichTextConfiguration getConfiguration() {
        return richTextConfiguration;
    }

    public boolean isShowSaveButton() {
        return showSaveButton;
    }
}
