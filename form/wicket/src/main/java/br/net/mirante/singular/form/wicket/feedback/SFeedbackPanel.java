/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.feedback;

import org.opensingular.singular.form.SFormUtil;
import org.opensingular.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;

public class SFeedbackPanel extends BSFeedbackPanel {

    public SFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter) {
        super(id, fence, filter);
    }
    public SFeedbackPanel(String id, Component fence) {
        super(id, fence);
    }
    public SFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }
    public SFeedbackPanel(String id) {
        super(id);
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Component component = super.newMessageDisplayComponent(id, message);
        if (component instanceof Label) {
            final Label label = (Label) component;

            if (message instanceof SFeedbackMessage) {
                final SFeedbackMessage bfm = (SFeedbackMessage) message;

                final SInstance instance = bfm.getInstanceModel().getObject();
                final SInstance parentContext = WicketFormUtils.resolveInstance(getFence()).orElse(null);
                final String labelPath = StringUtils.defaultString(
                    WicketFormUtils.generateTitlePath(getFence(), parentContext, message.getReporter(), instance),
                    SFormUtil.generatePath(instance, it -> it == parentContext));

                label.setDefaultModelObject(labelPath + " : " + bfm.getMessage());
            }
        }
        return component;
    }
}
