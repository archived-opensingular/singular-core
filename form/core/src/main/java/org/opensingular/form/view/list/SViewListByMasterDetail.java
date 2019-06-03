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
import org.opensingular.form.SType;
import org.opensingular.form.enums.ModalSize;
import org.opensingular.form.view.ConfigurableModal;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;

import javax.annotation.Nullable;

public class SViewListByMasterDetail extends AbstractSViewListWithCustomColumns<SViewListByMasterDetail>
        implements ConfigurableModal<SViewListByMasterDetail> {

    private ButtonsMasterDetailConfig buttonsConfig;
    private boolean                   editFieldsInModalEnabled = true;
    private String                    newActionLabel           = "Adicionar";

    private String    editActionLabel = "Atualizar";
    private ModalSize modalSize;

    private String actionColumnLabel = "Ações";

    private SType<?> sortableColumn;
    private boolean  ascendingMode = true;
    private boolean  disableSort   = false;
    private String   enforcedValidationMessage;
    private boolean  enforceValidationOnAdd;

    private long rowsPerPage = 10L;

    /**
     * This method will disable the edition of the element's of the Master detail.
     *
     * @return <code>this</code>
     */
    public SViewListByMasterDetail disabledEditFieldsInModal() {
        this.editFieldsInModalEnabled = false;
        return this;
    }

    /**
     * This method will return if the edit fields is enable of not.
     *
     * @return True for enable the edit fields, false for not.
     */
    public boolean isEditEnabled() {
        return editFieldsInModalEnabled;
    }

    public SViewListByMasterDetail withActionColumnLabel(String actionColumnLabel) {
        this.actionColumnLabel = actionColumnLabel;
        return this;
    }

    public SViewListByMasterDetail withNewActionLabel(String actionLabel) {
        this.newActionLabel = actionLabel;
        return this;
    }

    public String getNewActionLabel() {
        return newActionLabel;
    }

    public SViewListByMasterDetail withEditActionLabel(String actionLabel) {
        this.editActionLabel = actionLabel;
        return this;
    }

    public String getEditActionLabel() {
        return editActionLabel;
    }

    public String getActionColumnLabel() {
        return actionColumnLabel;
    }

    @Override
    public ModalSize getModalSize() {
        return modalSize;
    }

    @Override
    public void setModalSize(ModalSize size) {
        this.modalSize = size;
    }

    /**
     * Method for choosen a default sortable Column.
     * Note: Will use ASC mode.
     *
     * @param sortableColumn The column that will be sortable in the initialize.
     * @return <code>this</code>
     */
    public SViewListByMasterDetail setSortableColumn(SType<?> sortableColumn) {
        return this.setSortableColumn(sortableColumn, true);
    }

    /**
     * @param sortableColumn The column that will be sortable in the initialize.
     * @param ascendingMode  True for ASC.
     *                       False for DESC.
     * @return <code>this</code>
     */
    public SViewListByMasterDetail setSortableColumn(SType<?> sortableColumn, boolean ascendingMode) {
        this.sortableColumn = sortableColumn;
        this.ascendingMode = ascendingMode;
        return this;
    }

    public SType<?> getSortableColumn() {
        return sortableColumn;
    }

    public boolean isAscendingMode() {
        return ascendingMode;
    }

    /**
     * Method for disabled the Sort of the columns.
     * <p>
     * Note: The method <code>#setSortableColumn</code> will continuing working.
     * <p>
     * Default: False [Enable sort].
     *
     * @param disableSort True will disabled.
     *                    False will enabled.
     * @return <code>this</code>
     */
    public SViewListByMasterDetail setDisableSort(boolean disableSort) {
        this.disableSort = disableSort;
        return this;
    }

    public boolean isDisableSort() {
        return disableSort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonsMasterDetailConfig getButtonsConfig() {
        if (buttonsConfig == null) {
            buttonsConfig = new ButtonsMasterDetailConfig();
        }
        return buttonsConfig;
    }

    /**
     * Configure the view button.
     *
     * @param visibleFor The logic for show the button of the row.
     *                   Null for enable in all cases.
     * @param hint       The hint of the button.
     *                   Null for use the default.
     * @param icon       The icon of the button.
     *                   Null for use the default.
     * @return <code>this</code>
     */
    public SViewListByMasterDetail configureViewButtonInEditionPerRow(@Nullable String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon) {
        getButtonsConfig().setViewButtonInEdition(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

    /**
     * @param visibleFor The predicate for show the button of the row.
     *                   For use this logic, the visibleEdit have to be true.
     * @return <code>this</code>
     */
    public SViewListByMasterDetail configureViewButtonInEditionPerRow(IPredicate<SInstance> visibleFor) {
        return configureViewButtonInEditionPerRow(ButtonsMasterDetailConfig.VISUALIZAR_HINT, visibleFor, null);
    }

    public SViewListByMasterDetail enableView() {
        return configureViewButtonInEditionPerRow(s -> true);
    }

    public SViewListByMasterDetail disableView() {
        return configureViewButtonInEditionPerRow(s -> false);
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
    public SViewListByMasterDetail enableEdit(String hint, @Nullable IPredicate<SInstance> visibleFor, @Nullable Icon icon) {
        getButtonsConfig().setEditButton(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

    public SViewListByMasterDetail enableEdit(@Nullable IPredicate<SInstance> visibleFor) {
        return enableEdit(ButtonsConfigWithInsert.INSERT_HINT, visibleFor, null);
    }

    public SViewListByMasterDetail disableEdit() {
        return enableEdit(s -> false);
    }

    public SViewListByMasterDetail enableEdit() {
        return enableEdit(s -> true);
    }

    /**
     * If set, adding invalid elements is now allowed.
     * Element SInstance must be valid to be added to the corresponding SIList.
     *
     * @param message message to be displayed when the list element is not valid.
     *                A null message disables the message exhibition
     * @return
     */
    public SViewListByMasterDetail enforceValidationOnAdd(String message) {
        this.enforcedValidationMessage = message;
        this.enforceValidationOnAdd = true;
        return this;
    }

    /**
     * If set, adding invalid elements is now allowed.
     * Element SInstance must be valid to be added to the corresponding SIList.
     *
     * @return
     */
    public SViewListByMasterDetail enforceValidationOnAdd() {
        return enforceValidationOnAdd("Não é possível adicionar enquanto houver correções a serem feitas");
    }

    public String getEnforcedValidationMessage() {
        return enforcedValidationMessage;
    }

    public boolean isEnforceValidationOnAdd() {
        return enforceValidationOnAdd;
    }

    public SViewListByMasterDetail setRowsPerPage(long rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
        return this;
    }

    public long getRowsPerPage() {
        return rowsPerPage;
    }
}
