/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.component;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.wicket.feedback.SFeedbackPanel;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;

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
