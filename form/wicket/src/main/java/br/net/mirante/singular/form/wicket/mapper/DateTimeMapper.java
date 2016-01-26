package br.net.mirante.singular.form.wicket.mapper;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.net.mirante.singular.form.wicket.WicketBuildContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MDateTimerView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoDataHora;
import br.net.mirante.singular.form.wicket.mapper.datetime.DateTimeContainer;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class DateTimeMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, IModel<? extends MInstancia> model,
                                 IModel<String> labelModel) {
        MDateTimerView dateTimerView = null;
        if(view instanceof MDateTimerView){
            dateTimerView = (MDateTimerView) view;
        }
        final DateTimeContainer dateTimeContainer = new DateTimeContainer(model.getObject().getNome(), new MInstanciaValorModel<>(model), dateTimerView);
        formGroup.appendDiv(dateTimeContainer);
        return dateTimeContainer;
    }


    @Override
    public FormComponent[] findAjaxComponents(Component input) {
        DateTimeContainer dateTimeContainer = (DateTimeContainer) input;
        List<FormComponent> formComponents = new ArrayList<>();
        dateTimeContainer.visitChildren(new IVisitor<Component, Object>() {
            @Override
            public void component(Component component, IVisit<Object> iVisit) {
                if (component instanceof FormComponent){
                    formComponents.add((FormComponent) component);
                    iVisit.dontGoDeeper();
                }
            }
        });
        return formComponents.toArray(new FormComponent[0]);
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends MInstancia> model) {
        final SimpleDateFormat format = new SimpleDateFormat(MTipoDataHora.FORMAT);
        if (model.getObject().getValor() instanceof Date) {
            return format.format(model.getObject().getValor());
        }
        return StringUtils.EMPTY;
    }
}
