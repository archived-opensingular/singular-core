package br.net.mirante.singular.form.wicket.mapper;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoDataHora;
import br.net.mirante.singular.form.wicket.mapper.datetime.DateTimeContainer;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class DateTimeMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, IModel<? extends MInstancia> model,
                                 IModel<String> labelModel) {
        final DateTimeContainer dateTimeContainer = new DateTimeContainer(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendDiv(dateTimeContainer);
        return dateTimeContainer;
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
