/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.feedback;

import org.opensingular.form.SInstance;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class SFeedbackMessage extends FeedbackMessage {

    private final IModel<? extends SInstance> instanceModel;

    public SFeedbackMessage(
        Component reporter,
        Serializable message,
        int level,
        IModel<? extends SInstance> instanceModel) {
        super(reporter, message, level);
        this.instanceModel = instanceModel;
    }

    public IModel<? extends SInstance> getInstanceModel() {
        return instanceModel;
    }
    public SInstance getInstance() {
        IModel<? extends SInstance> model = getInstanceModel();
        return (getInstanceModel() == null) ? null : model.getObject();
    }
}
