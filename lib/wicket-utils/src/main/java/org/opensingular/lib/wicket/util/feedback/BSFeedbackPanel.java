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

package org.opensingular.lib.wicket.util.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

@SuppressWarnings("serial")
public class BSFeedbackPanel extends FencedFeedbackPanel {

    private final Component fence;

    public BSFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
        this.fence = null;
    }

    public BSFeedbackPanel(String id) {
        super(id);
        this.fence = null;
    }

    public BSFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter) {
        super(id, fence, filter);
        this.fence = fence;
    }

    public BSFeedbackPanel(String id, Component fence) {
        super(id, fence);
        this.fence = fence;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
    }

    public Component getFence() {
        return fence;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(anyMessage());
    }
}
