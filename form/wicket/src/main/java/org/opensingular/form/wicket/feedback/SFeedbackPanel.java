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

package org.opensingular.form.wicket.feedback;

import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;
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
