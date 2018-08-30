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

public class SViewListByTable extends AbstractSViewListWithControls<SViewListByTable> {

    private boolean renderCompositeFieldsAsColumns = true;
    private boolean editVisible = false; //This variable is used to determine if will have a column for edit.
    private ButtonsConfigWithInsert buttonsConfig;

    public boolean isRenderCompositeFieldsAsColumns() {
        return renderCompositeFieldsAsColumns;
    }

    public SViewListByTable setRenderCompositeFieldsAsColumns(boolean renderCompositeFieldsAsColumns) {
        this.renderCompositeFieldsAsColumns = renderCompositeFieldsAsColumns;
        return this;
    }

    /**
     * Configure the edit button.
     * This button will able to insert new line.
     *
     * @param visibleFor  The logic for show the button of the row.
     *                    Null for enable in all cases.
     * @param hint        The hint of the button.
     *                    Null for use the default.
     * @param icon        The icon of the button.
     *                    Null for use the default.
     * @param visibleEdit False will override the Predicate logic and will hide the button for all rows.
     *                    True will use the predicate logic.
     * @return <code>this</code>
     */
    public SViewListByTable enableInsert(@Nullable String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon, boolean visibleEdit) {
        this.editVisible = visibleEdit;
        getButtonsConfig().setInsertButton(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

    public SViewListByTable enableInsert(@Nullable String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon) {
        return enableInsert(hint, visibleFor, icon, true);
    }

    public SViewListByTable enableInsert(@Nullable IPredicate<SInstance> visibleFor) {
        return enableInsert(ButtonsConfigWithInsert.INSERT_HINT, visibleFor, null, true);
    }

    /**
     * By default the insert line is disable.
     */
    public SViewListByTable disableInsert() {
        this.editVisible = false;
        return enableInsert(s -> false);
    }

    public SViewListByTable enableInsert() {
        this.editVisible = true;
        return enableInsert(s -> true);
    }

    /**
     * If edit button can be visible this method will return true.
     *
     * @return True if edit button could be visible. False if the edit button will never be visible.
     */
    public boolean isEditVisible() {
        return editVisible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonsConfigWithInsert getButtonsConfig() {
        if (buttonsConfig == null) {
            buttonsConfig = new ButtonsConfigWithInsert();
        }
        return buttonsConfig;
    }

}
