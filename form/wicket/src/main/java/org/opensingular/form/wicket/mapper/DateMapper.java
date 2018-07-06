/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.date.SViewDate;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SIDateTimeModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.behavior.DatePickerSettings;
import org.opensingular.lib.wicket.util.behavior.SingularDatePickerSettings;

import static org.opensingular.form.type.basic.SPackageBasic.ATR_MAX_DATE;
import static org.opensingular.form.type.basic.SPackageBasic.ATR_MIN_DATE;
import static org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants.DEFAULT_DATE_FORMAT;
import static org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants.DEFAULT_END_DATE;
import static org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants.DEFAULT_START_DATE;

@SuppressWarnings("serial")
public class DateMapper extends AbstractDateMapper {

    @Override
    protected Map<String, ? extends Serializable> getOptions(IModel<? extends SInstance> model) {
        Map<String, Serializable> attrs = new HashMap<>();
        attrs.put("data-date-start-date", configureMinDate(model));
        attrs.put("data-date-end-date", configureMaxDate(model));
        return attrs;
    }

    @Override
    protected TextField<String> getInputData(IModel<? extends SInstance> model) {
        return new TextField("date", new SIDateTimeModel.DateModel(new SInstanceValueModel<>(model)))/* {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new DateConverter(defaultDateFormat());
            }
        }*/;
    }

    private String configureMinDate(IModel<? extends SInstance> model) {
        Date date = model.getObject().getAttributeValue(ATR_MIN_DATE);
        if (date == null) {
            return DEFAULT_START_DATE;
        }
        return defaultDateFormat().format(date);
    }

    private String configureMaxDate(IModel<? extends SInstance> model) {
        Date date = model.getObject().getAttributeValue(ATR_MAX_DATE);
        if (date == null) {
            return DEFAULT_END_DATE;
        }
        return defaultDateFormat().format(date);
    }

    @Override
    public DatePickerSettings getDatePickerSettings(WicketBuildContext ctx) {
        return new SingularDatePickerSettings(ctx.getViewSupplier(SViewDate.class), ctx.getModel());
    }

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        if ((model != null) && (model.getObject() != null)) {
            SInstance instance = model.getObject();
            if (instance.getValue() instanceof Date) {
                Date dt = (Date) instance.getValue();
                final SimpleDateFormat formatter = defaultDateFormat();
                return formatter.format(dt);
            }
        }
        return StringUtils.EMPTY;
    }


    public static SimpleDateFormat defaultDateFormat() {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    }

}
