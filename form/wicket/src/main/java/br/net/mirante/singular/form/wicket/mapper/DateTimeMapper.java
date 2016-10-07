/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.STypeDateTime;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewDateTime;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.datetime.DateTimeContainer;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class DateTimeMapper extends AbstractControlsFieldComponentMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SView view = ctx.getView();
        
        SViewDateTime dateTimerView = null;
        if(view instanceof SViewDateTime){
            dateTimerView = (SViewDateTime) view;
        }
        final DateTimeContainer dateTimeContainer = new DateTimeContainer(model.getObject().getName(), new SInstanceValueModel<>(model), dateTimerView);
        formGroup.appendDiv(dateTimeContainer);
        return dateTimeContainer;
    }


    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SimpleDateFormat format = new SimpleDateFormat(STypeDateTime.FORMAT);
        if (model.getObject().getValue() instanceof Date) {
            return format.format(model.getObject().getValue());
        }
        return StringUtils.EMPTY;
    }
}
