package org.opensingular.form.wicket.feedback;

import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractSValidationFeedbackPanel extends Panel implements IFeedback {

    private final FeedbackFence fence;

    public AbstractSValidationFeedbackPanel(String id, FeedbackFence fence) {
        super(id);
        this.fence = fence;
    }

    public FeedbackFence getFence() {
        return fence;
    }

}