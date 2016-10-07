/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
