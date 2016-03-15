/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.ajax;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class AjaxErrorEventPayload {
    private final AjaxRequestTarget target;
    public AjaxErrorEventPayload(AjaxRequestTarget target) {
        this.target = target;
    }
    public AjaxRequestTarget getTarget() {
        return target;
    }
}
