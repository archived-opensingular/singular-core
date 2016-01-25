package br.net.mirante.singular.form.wicket.mapper.datetime;

import java.util.Date;

import org.apache.wicket.markup.html.form.TextField;

import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.MIDateTimeModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

public class DateTimeContainer extends BSContainer<DateTimeContainer> {

    private final IMInstanciaAwareModel<Date> model;

    public DateTimeContainer(String id, IMInstanciaAwareModel<Date> model) {
        super(id);
        this.model = model;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final TemplatePanel template = buildTemplatePanel();
        template.add(new TextField<>("date", new MIDateTimeModel.DateModel(model)));
        template.add(new TextField<>("time", new MIDateTimeModel.TimeModel(model)));
    }

    protected TemplatePanel buildTemplatePanel() {
        return newTemplateTag(tt -> {
            final StringBuilder templateBuildr = new StringBuilder();
            templateBuildr.append(" <div class='input-group'> ");
            templateBuildr.append("    <input wicket:id='date' type='text' class='form-control date date-picker' ");
            templateBuildr.append("      data-date-format='dd/mm/yyyy' data-date-start-date='01/01/1900' ");
            templateBuildr.append("      data-date-end-date='31/12/2999' data-date-start-view='days' ");
            templateBuildr.append("      data-date-min-view-mode='days'> ");
            templateBuildr.append("    <span class='input-group-addon' style='width: 0; padding: 0; border: none;'></span> ");
            templateBuildr.append("    <input wicket:id='time' type='text' class='form-control timepicker'> ");
            templateBuildr.append(" </div> ");
            return templateBuildr.toString();
        });
    }

}
