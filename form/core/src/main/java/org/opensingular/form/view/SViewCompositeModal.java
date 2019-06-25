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

public class SViewCompositeModal extends SView implements ConfigurableViewModal<SViewCompositeModal> {

    private String    editActionLabel          = "Editar";
    private String    viewActionLabel          = "Visualizar";
    private ModalSize modalSize                = ModalSize.LARGE;
    private boolean   validateAllLineOnConfirmAndCancel;
    private boolean   editFieldsInModalEnabled = true;

    public SViewCompositeModal withEditActionLabel(String editActionLabel) {
        this.editActionLabel = editActionLabel;
        return this;
    }

    public SViewCompositeModal setValidateAllLineOnConfirmAndCancel(boolean validateAllLineOnConfirmAndCancel) {
        this.validateAllLineOnConfirmAndCancel = validateAllLineOnConfirmAndCancel;
        return this;
    }

    public boolean isValidateAllLineOnConfirmAndCancel() {
        return validateAllLineOnConfirmAndCancel;
    }


    public String getEditActionLabel() {
        return editActionLabel;
    }

    public SViewCompositeModal disabledEditFieldsInModal() {
        this.editFieldsInModalEnabled = false;
        return this;
    }

    public boolean isEditEnabled() {
        return editFieldsInModalEnabled;
    }

    public ModalSize getModalSize() {
        return modalSize;
    }

    @Override
    public void setModalSize(ModalSize size) {
        this.modalSize = modalSize;
    }


    public boolean isEnforceValidationOnAdd() {
        return false;
    }


    public String getEnforcedValidationMessage() {
        return null;
    }

    public String getViewActionLabel() {
        return viewActionLabel;
    }

    public SViewCompositeModal withViewActionLabel(String viewActionLabel) {
        this.viewActionLabel = viewActionLabel;
        return this;
    }
}
