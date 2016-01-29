package br.net.mirante.singular.form.wicket.feedback;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;

public class BFeedbackMessage extends FeedbackMessage {

    private final IModel<? extends SInstance> instanceModel;

    public BFeedbackMessage(
        Component reporter,
        Serializable message,
        int level,
        IModel<? extends SInstance> instanceModel) {
        super(reporter, message, level);
        this.instanceModel = instanceModel;
    }

    public IModel<? extends SInstance> getInstanceModel() {
        return instanceModel;
    }
    public SInstance getInstance() {
        IModel<? extends SInstance> model = getInstanceModel();
        return (getInstanceModel() == null) ? null : model.getObject();
    }
}
