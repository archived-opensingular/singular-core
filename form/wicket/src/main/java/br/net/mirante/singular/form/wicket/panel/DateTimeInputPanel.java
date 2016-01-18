package br.net.mirante.singular.form.wicket.panel;

import java.util.Date;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.MIDateTimeModel;

public class DateTimeInputPanel extends Panel {

    private final IMInstanciaAwareModel<Date> model;

    public DateTimeInputPanel(String id, IMInstanciaAwareModel<Date> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TextField<>("date", new MIDateTimeModel.DateModel(model)));
        add(new TextField<>("time", new MIDateTimeModel.TimeModel(model)));
    }
}
