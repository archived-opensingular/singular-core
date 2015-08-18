package br.net.mirante.singular.util.wicket.feedback;

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