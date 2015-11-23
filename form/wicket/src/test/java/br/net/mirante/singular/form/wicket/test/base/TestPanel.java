package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class TestPanel extends Panel {

    private Component container;
    private BSContainer bodyContainer;

    public TestPanel(String id) {
        super(id);
        container = buildContainer("container");
        bodyContainer = buildBodyContainer("body-container");
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
