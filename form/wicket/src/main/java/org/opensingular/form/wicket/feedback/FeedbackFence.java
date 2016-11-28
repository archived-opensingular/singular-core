package org.opensingular.form.wicket.feedback;

import org.apache.wicket.Component;

import java.io.Serializable;

public class FeedbackFence implements Serializable {

    private Component mainContainer;
    private Component externalContainer;

    public FeedbackFence(Component mainContainer) {
        this.mainContainer = mainContainer;
    }

    public FeedbackFence(Component mainContainer, Component externalContainer) {
        this.mainContainer = mainContainer;
        this.externalContainer = externalContainer;
    }

    public Component getMainContainer() {
        return mainContainer;
    }

    public Component getExternalContainer() {
        return externalContainer;
    }

}