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

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;

public class ItemActionConfirmation implements Serializable {

    private String title;
    private String confirmationMessage;
    private String cancelButtonLabel;
    private String confirmationButtonLabel;
    private String selectEndpoint;

    public ItemActionConfirmation() {
    }

    public ItemActionConfirmation(String title, String confirmationMessage,
                                  String cancelButtonLabel, String confirmationButtonLabel,
                                  String selectEndpoint) {
        this.title = title;
        this.confirmationMessage = confirmationMessage;
        this.cancelButtonLabel = cancelButtonLabel;
        this.confirmationButtonLabel = confirmationButtonLabel;
        this.selectEndpoint = selectEndpoint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

    public String getCancelButtonLabel() {
        return cancelButtonLabel;
    }

    public void setCancelButtonLabel(String cancelButtonLabel) {
        this.cancelButtonLabel = cancelButtonLabel;
    }

    public String getConfirmationButtonLabel() {
        return confirmationButtonLabel;
    }

    public void setConfirmationButtonLabel(String confirmationButtonLabel) {
        this.confirmationButtonLabel = confirmationButtonLabel;
    }

    public String getSelectEndpoint() {
        return selectEndpoint;
    }

    public void setSelectEndpoint(String selectEndpoint) {
        this.selectEndpoint = selectEndpoint;
    }
}
