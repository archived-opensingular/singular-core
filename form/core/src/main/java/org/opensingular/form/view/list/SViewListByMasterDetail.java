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
import org.opensingular.form.enums.ButtonsMasterDetailConfig;
import org.opensingular.form.enums.ModalSize;
import org.opensingular.form.view.ConfigurableModal;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;

public class SViewListByMasterDetail extends AbstractSViewListWithCustomColumns<SViewListByMasterDetail>
        implements ConfigurableModal<SViewListByMasterDetail> {

    private boolean editEnabled = true;
    private String newActionLabel = "Adicionar";

    private String editActionLabel = "Atualizar";
    private ModalSize modalSize;

    private String actionColumnLabel = "Ações";

    private SType<?> sortableColumn;
    private boolean ascendingMode = true;
    private boolean disableSort = false;

    public SViewListByMasterDetail disableEdit() {
        this.editEnabled = false;
        return this;
    }

    public boolean isEditEnabled() {
        return editEnabled;
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

    public ButtonsMasterDetailConfig getButtonsConfig() {
        return new ButtonsMasterDetailConfig();
    }

    public SViewListByMasterDetail configureViewButton(String hint, IPredicate<SInstance> visibleFor, Icon icon) {
        getButtonsConfig().setViewButton(new ButtonAction(visibleFor, hint, icon));
        return this;
    }

}
