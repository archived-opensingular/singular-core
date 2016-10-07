/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.ajax;

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
