package br.net.mirante.singular.util.wicket.output;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class BOutputPanel extends Panel {

    private final IModel<String> outputText;

    public BOutputPanel(String id, IModel<String> outputText) {
        super(id);
        this.outputText = outputText;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final BSWellBorder well = BSWellBorder.small("well");
        add(well.add(new Label("output", outputText)));
    }
}
