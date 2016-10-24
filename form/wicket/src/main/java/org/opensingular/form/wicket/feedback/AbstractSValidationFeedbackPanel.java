package org.opensingular.form.wicket.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractSValidationFeedbackPanel extends Panel implements IFeedback {

    private final Component fence;

    public AbstractSValidationFeedbackPanel(String id, Component fence) {
        super(id);
        this.fence = fence;
    }

    public Component getFence() {
        return fence;
    }
}