/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.flow.rest;

public class ActionResponse {

    private String resultMessage;
    private boolean successful;

    public ActionResponse() {
    }

    public ActionResponse(String resultMessage, boolean successful) {
        this.resultMessage = resultMessage;
        this.successful = successful;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
