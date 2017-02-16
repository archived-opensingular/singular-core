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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerInputGroup;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import static org.opensingular.form.type.basic.SPackageBasic.ATR_MAX_DATE;

@SuppressWarnings("serial")
public class DateMapper extends AbstractControlsFieldComponentMapper {

    private static final Logger LOGGER = Logger.getLogger(DateMapper.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        BSDatepickerInputGroup datepicker = formGroup
                .newComponent(id -> new BSDatepickerInputGroup(id)
                        .setConverter(new ConverterImpl())
                        .setTextFieldConfigurer((IConsumer<FormComponent<?>>) c -> c
                                .setLabel(labelModel)
                                .setDefaultModel(new SInstanceValueModel<>(model))
                                .setOutputMarkupId(true)
                                .add(new InputMaskBehavior(Masks.FULL_DATE))));

        configureMaxDate(datepicker, model.getObject().getAttributeValue(ATR_MAX_DATE));

        return datepicker.getTextField();
    }

    private void configureMaxDate(BSDatepickerInputGroup datepicker, Date maxDate) {
        if(maxDate != null){
            datepicker.setEndDate(defaultDateFormat().format(maxDate));
        }
    }

    private static SimpleDateFormat defaultDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        adjustJSEvents(component);
        BSDatepickerInputGroup datepicker = BSDatepickerInputGroup.getFromTextfield(component);
        datepicker.getTextField()
                .add(AjaxUpdateInputBehavior.forProcess(model, listener))
                .add(AjaxUpdateInputBehavior.forValidate(model, listener));
    }

    @Override
    public void adjustJSEvents(Component comp) {
        BSDatepickerInputGroup datepicker = BSDatepickerInputGroup.getFromTextfield(comp);
        Component textField = datepicker.getTextField();
        textField
                .add(new SingularEventBehavior()
                        .setProcessEvent("changeDate", datepicker)
                        .setValidateEvent("blur", textField)
                        .setSupportComponents(datepicker.getButton()));
    }

    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        if ((model != null) && (model.getObject() != null)) {
            SInstance instancia = model.getObject();
            if (instancia.getValue() instanceof Date) {
                Date                   dt         = (Date) instancia.getValue();
                final SimpleDateFormat formattter = defaultDateFormat();
                return formattter.format(dt);
            }
        }
        return StringUtils.EMPTY;
    }

    @SuppressWarnings("rawtypes")
    private static final class ConverterImpl implements IConverter {
        @Override
        public Object convertToObject(String date, Locale locale) throws ConversionException {
            if ("//".equals(date))
                return null;
            try {
                SimpleDateFormat sdf = defaultDateFormat();
                sdf.setLenient(false);
                return sdf.parse(date);
            } catch (ParseException e) {
                String msg = String.format(
                        "Can't parse value '%s' with format '%s'.",
                        date, "dd/MM/yyyy");
                LOGGER.log(Level.WARNING, msg, e);
                throw new ConversionException(e);
            }
        }

        @Override
        public String convertToString(Object date, Locale locale) {
            return (defaultDateFormat()).format((Date) date);
        }
    }

}