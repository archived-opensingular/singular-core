package org.opensingular.form.wicket.test.base;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;

public abstract class TestPanel extends Panel {

    private Component container;
    private BSContainer bodyContainer;

    public TestPanel(String id) {
        super(id);
        container = buildContainer("container");
        bodyContainer = buildBodyContainer("body-container");
        bodyContainer.setOutputMarkupId(true);
        add(container);
        add(bodyContainer);
    }

    public abstract Component buildContainer(String id);


    public BSContainer buildBodyContainer(String id) {
        return new BSContainer(id);
    }

    public Component getContainer() {
        return container;
    }

    public BSContainer getBodyContainer() {
        return bodyContainer;
    }
}
