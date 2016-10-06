/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

@SuppressWarnings("serial")
public class NotContainedFeedbackMessageFilter implements IFeedbackMessageFilter {

    private final MarkupContainer container;

    public NotContainedFeedbackMessageFilter(MarkupContainer container) {
        this.container = container;
    }

    @Override
    public boolean accept(FeedbackMessage message) {
        Component reporter = message.getReporter();
        return (reporter == null) || (container != null && !container.contains(reporter, true));
    }
}   