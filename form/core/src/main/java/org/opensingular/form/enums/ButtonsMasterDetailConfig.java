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

package org.opensingular.form.enums;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.list.ButtonAction;
import org.opensingular.form.view.list.ButtonsConfig;

public class ButtonsMasterDetailConfig extends ButtonsConfig {

    public static final String VISUALIZAR_HINT = "Visualizar";
    /**
     * The visible button rule visible will be used just in the edition, in the view mode will always be visible.
     */
    private ButtonAction viewButtonInEdition = new ButtonAction(f -> false, VISUALIZAR_HINT, null);

    public ButtonsMasterDetailConfig() {
        super();
        setEditButton(new ButtonAction(f -> true, EDITAR_HINT, null));
    }

    public ButtonAction getViewButtonInEdition() {
        return viewButtonInEdition;
    }

    public void setViewButtonInEdition(ButtonAction viewButtonInEdition) {
        this.viewButtonInEdition = viewButtonInEdition;
    }

    public boolean isViewEnabled(SInstance instance) {
        return viewButtonInEdition.isEnabled(instance);
    }
}
