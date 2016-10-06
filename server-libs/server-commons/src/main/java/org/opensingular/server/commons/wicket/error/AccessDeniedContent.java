/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.wicket.error;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import org.apache.wicket.model.IModel;

import org.opensingular.server.commons.wicket.view.template.Content;

public class AccessDeniedContent extends Content {

    public AccessDeniedContent(String id) {
        super(id);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("");
    }
}
