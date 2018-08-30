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


    public boolean isRenderCompositeFieldsAsColumns() {
        return renderCompositeFieldsAsColumns;
    }

    public SViewListByTable setRenderCompositeFieldsAsColumns(boolean renderCompositeFieldsAsColumns) {
        this.renderCompositeFieldsAsColumns = renderCompositeFieldsAsColumns;
        return this;
    }

    /**
     * @param visibleFor  The predicate for show the button of the row.
     *                    For use this logic, the visibleEdit have to be true.
     * @param visibleEdit False will override the Predicate logic and will hide the button for all rows.
     *                    True will use the predicate logic.
     * @return <code>this</code>
     */
    public SViewListByTable configureEditButtonPerRow(@Nullable IPredicate<SInstance> visibleFor, boolean visibleEdit) {
        return configureEditButtonPerRow(ButtonsConfig.EDITAR_HINT, visibleFor, null, visibleEdit);
    }

    /**
     * Configure the edit button.
     * This button will able to insert new line.
     *
     * @param visibleFor  The logic for show the button of the row.
     *                    Null for enable in all cases.
     * @param hint        The hint of the button.
     * @param icon        The icon of the button.
     *                    Null for use the default.
     * @param visibleEdit False will override the Predicate logic and will hide the button for all rows.
     *                    True will use the predicate logic.
     * @return <code>this</code>
     */
    public SViewListByTable configureEditButtonPerRow(String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon, boolean visibleEdit) {
        this.editVisible = visibleEdit;
        getButtonsConfig().setEditButton(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SViewListByTable configureEditButtonPerRow(String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon) {
        this.editVisible = true;
        return super.configureEditButtonPerRow(hint, visibleFor, icon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SViewListByTable configureEditButtonPerRow(@Nullable IPredicate<SInstance> visibleFor) {
        this.editVisible = true;
        return super.configureEditButtonPerRow(visibleFor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SViewListByTable disableInsert() {
        this.editVisible = false;
        return configureEditButtonPerRow(ButtonsConfig.EDITAR_HINT, s -> false, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SViewListByTable enableInsert() {
        this.editVisible = true;
        return configureEditButtonPerRow(ButtonsConfig.EDITAR_HINT, s -> true, null);
    }

    /**
     * If edit button can be visible this method will return true.
     *
     * @return True if edit button could be visible. False if the edit button will never be visible.
     */
    public boolean isEditVisible() {
        return editVisible;
    }
}
