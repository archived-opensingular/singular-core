package br.net.mirante.singular.form.wicket.feedback;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance2;

public class BFeedbackMessage extends FeedbackMessage {

    private final IModel<? extends SInstance2> instanceModel;

    public BFeedbackMessage(
        Component reporter,
        Serializable message,
        int level,
        IModel<? extends SInstance2> instanceModel) {
        super(reporter, message, level);
        this.instanceModel = instanceModel;
    }

    public IModel<? extends SInstance2> getInstanceModel() {
        return instanceModel;
    }
    public SInstance2 getInstance() {
        IModel<? extends SInstance2> model = getInstanceModel();
        return (getInstanceModel() == null) ? null : model.getObject();
    }
}
