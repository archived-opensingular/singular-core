package br.net.mirante.singular.form.wicket.feedback;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;

import br.net.mirante.singular.form.mform.MFormUtil;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;

public class BFFeedbackPanel extends BSFeedbackPanel {

    public BFFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter) {
        super(id, fence, filter);
    }
    public BFFeedbackPanel(String id, Component fence) {
        super(id, fence);
    }
    public BFFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }
    public BFFeedbackPanel(String id) {
        super(id);
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Component component = super.newMessageDisplayComponent(id, message);
        if (component instanceof Label) {
            final Label label = (Label) component;

            if (message instanceof BFeedbackMessage) {
                final BFeedbackMessage bfm = (BFeedbackMessage) message;

                final SInstance instance = bfm.getInstanceModel().getObject();
                final SInstance parentContext = WicketFormUtils.resolveInstance(getFence()).orElse(null);
                final String labelPath = StringUtils.defaultString(
                    MFormUtil.generateUserFriendlyPath(instance, parentContext),
                    MFormUtil.generatePath(instance, it -> it == parentContext));

                label.setDefaultModelObject(labelPath + " : " + bfm.getMessage());
            }
        }
        return component;
    }
}
