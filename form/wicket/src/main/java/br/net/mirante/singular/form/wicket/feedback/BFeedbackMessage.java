package br.net.mirante.singular.form.wicket.feedback;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public class BFeedbackMessage extends FeedbackMessage {

    private final IModel<? extends MInstancia> instanceModel;

    public BFeedbackMessage(
        Component reporter,
        Serializable message,
        int level,
        IModel<? extends MInstancia> instanceModel) {
        super(reporter, message, level);
        this.instanceModel = instanceModel;
    }

    public IModel<? extends MInstancia> getInstanceModel() {
        return instanceModel;
    }
    public MInstancia getInstance() {
        IModel<? extends MInstancia> model = getInstanceModel();
        return (getInstanceModel() == null) ? null : model.getObject();
    }
}
