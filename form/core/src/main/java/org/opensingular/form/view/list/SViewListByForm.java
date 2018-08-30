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
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;

import javax.annotation.Nullable;

public class SViewListByForm extends AbstractSViewListWithControls<SViewListByForm> {

    private ButtonsConfigWithInsert buttonsConfig;

    /**
     * Uses the displayString defined in the list element type as header for each form panel
     */
    public SViewListByForm() {
    }


    /**
     * Configure the new insert button per line.
     * <p>
     *
     * @param visibleFor The logic for show the button of the row.
     *                   Null for enable in all cases.
     * @param hint       The hint of the button.
     * @param icon       The icon of the button.
     *                   Null for use the default.
     * @return <code>this</code>
     */
    public SViewListByForm enableInsert(String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon) {
        getButtonsConfig().setInsertButton(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

    public SViewListByForm enableInsert(@Nullable IPredicate<SInstance> visibleFor) {
        return enableInsert(ButtonsConfigWithInsert.INSERT_HINT, visibleFor, null);
    }

    public SViewListByForm disableInsert() {
        return enableInsert(s -> false);
    }

    public SViewListByForm enableInsert() {
        return enableInsert(s -> true);
    }

    /** {@inheritDoc} */
    @Override
    public ButtonsConfigWithInsert getButtonsConfig() {
        if (buttonsConfig == null) {
            buttonsConfig = new ButtonsConfigWithInsert();
        }
        return buttonsConfig;
    }

}
