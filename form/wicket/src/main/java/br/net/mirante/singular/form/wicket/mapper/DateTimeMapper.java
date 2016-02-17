package br.net.mirante.singular.form.wicket.mapper;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.MDateTimerView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.STypeDataHora;
import br.net.mirante.singular.form.wicket.mapper.datetime.DateTimeContainer;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class DateTimeMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, IModel<? extends SInstance> model,
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
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SimpleDateFormat format = new SimpleDateFormat(STypeDataHora.FORMAT);
        if (model.getObject().getValue() instanceof Date) {
            return format.format(model.getObject().getValue());
        }
        return StringUtils.EMPTY;
    }
}
