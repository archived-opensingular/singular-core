/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
