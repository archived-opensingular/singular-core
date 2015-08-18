package br.net.mirante.singular.util.wicket.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

@SuppressWarnings("serial")
public class BSFeedbackPanel extends FencedFeedbackPanel {

    public BSFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    public BSFeedbackPanel(String id) {
        super(id);
    }

    public BSFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter) {
        super(id, fence, filter);
    }

    public BSFeedbackPanel(String id, Component fence) {
        super(id, fence);
    }
}
