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

package org.opensingular.form.view.list;

import org.opensingular.form.SInstance;

/**
 * This class is responsible for configure the buttons of the Tables component's.
 * Note: This configuraion is used just in the EDIT MODE. <code>ViewMode#EDIT</code>
 *
 * @see SViewListByMasterDetail
 * @see SViewListByTable
 * @see SViewListByForm
 */
public class ButtonsConfig {

    public static final String REMOVER_HINT = "Remover";

    private ButtonAction deleteButton;

    /**
     * By default the Edit button will always be invisible,
     * and the delete button will show.
     */
    public ButtonsConfig() {
        deleteButton = new ButtonAction(f -> true, REMOVER_HINT, null);
    }

    public ButtonAction getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(ButtonAction deleteButton) {
        this.deleteButton = deleteButton;
    }

    /**
     * Verify is delete button is enabled by the instance of the row.
     *
     * @param instance The instance of the row.
     * @return True for enable, false for not.
     */
    public boolean isDeleteEnabled(SInstance instance) {
        return deleteButton.isEnabled(instance);
    }

}
