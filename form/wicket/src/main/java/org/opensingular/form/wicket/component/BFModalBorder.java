/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.component;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.feedback.SFeedbackPanel;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class BFModalBorder extends BSModalBorder {

    public BFModalBorder(String id, IModel<?> model) {
        super(id, model);
    }
    public BFModalBorder(String id) {
        super(id);
    }

    @Override
    protected BSFeedbackPanel newFeedbackPanel(String id, BSModalBorder fence, IFeedbackMessageFilter messageFilter) {
        return new SFeedbackPanel(id, fence, messageFilter);
    }
}
