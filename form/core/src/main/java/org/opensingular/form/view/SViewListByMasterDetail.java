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

package org.opensingular.form.view;

import org.opensingular.form.enums.ModalSize;

public class SViewListByMasterDetail extends AbstractSViewListWithCustomColumns<SViewListByMasterDetail>
        implements ConfigurableModal<SViewListByMasterDetail> {

    private boolean editEnabled = true;
    private String newActionLabel = "Adicionar";

    private String editActionLabel = "Atualizar";
    private ModalSize modalSize;

    private String actionColumnLabel = "Ações";

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
}
