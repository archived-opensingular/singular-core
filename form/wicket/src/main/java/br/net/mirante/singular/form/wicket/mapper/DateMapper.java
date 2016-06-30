/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

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

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.datepicker.BSDatepickerInputGroup;

@SuppressWarnings("serial")
public class DateMapper extends ControlsFieldComponentAbstractMapper {

    private static final Logger LOGGER = Logger.getLogger(DateMapper.class.getName());

    @Override
    public Component appendInput() {
        BSDatepickerInputGroup datepicker = formGroup
            .newComponent(id -> (BSDatepickerInputGroup) new BSDatepickerInputGroup(id)
                .setConverter(new ConverterImpl())
                .setTextFieldConfigurer((FormComponent<?> c) -> c
                    .setLabel(labelModel)
                    .setDefaultModel(new MInstanciaValorModel<>(model))
                    .setOutputMarkupId(true)
                    .add(new InputMaskBehavior(Masks.FULL_DATE))));
        return datepicker.getTextField();
    }

    @Override
    public void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        adjustJSEvents(component);
        BSDatepickerInputGroup datepicker = BSDatepickerInputGroup.getFromTextfield(component);
        datepicker.getTextField()
            .add(AjaxUpdateInputBehavior.forProcess(model, listener))
            .add(AjaxUpdateInputBehavior.forValidate(model, listener));
        //datepicker.getTextField().add(AjaxUpdateInputBehavior.forProcess(model, listener));
    }

    @Override
    public void adjustJSEvents(Component comp) {
        BSDatepickerInputGroup datepicker = BSDatepickerInputGroup.getFromTextfield(comp);
        datepicker.getTextField()
            .add(new SingularEventBehavior()
                .setProcessEvent("changeDate", datepicker)
                .setValidateEvent("blur", datepicker.getTextField())
                .setSupportComponents(datepicker.getButton()));
    }

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        if ((model != null) && (model.getObject() != null)) {
            SInstance instancia = model.getObject();
            if (instancia.getValue() instanceof Date) {
                Date dt = (Date) instancia.getValue();
                final SimpleDateFormat formattter = new SimpleDateFormat("dd/MM/yyyy");
                return formattter.format(dt);
            }
        }
        return StringUtils.EMPTY;
    }

    private static final class ConverterImpl implements IConverter {
        @Override
        public Object convertToObject(String date, Locale locale) throws ConversionException {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
            return (new SimpleDateFormat("dd/MM/yyyy")).format((Date) date);
        }
    }

    //    private static final class BSDatepickerAjaxUpdateBehavior extends AjaxUpdateInputBehavior {
    //
    //        private transient boolean flag;
    //
    //        private BSDatepickerAjaxUpdateBehavior(IModel<SInstance> model, boolean validateOnly, IAjaxUpdateListener listener) {
    //            super((validateOnly) ? SINGULAR_VALIDATE_EVENT : SINGULAR_PROCESS_EVENT, model, validateOnly, listener);
    //        }
    //
    //        @Override
    //        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    //            super.updateAjaxAttributes(attributes);
    //            if (flag)
    //                attributes.setEventNames();
    //        }
    //
    //        @Override
    //        protected CharSequence getCallbackScript(Component component) {
    //            flag = true;
    //            try {
    //                return JQuery.on(component, super.getEvent(), super.getCallbackScript(component));
    //            } finally {
    //                flag = false;
    //            }
    //        }
    //
    //        @Override
    //        public void renderHead(Component component, IHeaderResponse response) {
    //            super.renderHead(component, response);
    //            response.render(OnDomReadyHeaderItem.forScript(JQuery.$(getComponent())
    //                + ".on('" + BSDatepickerConstants.JS_CHANGE_EVENT + "', function(){ $(this).trigger('" + SINGULAR_VALIDATE_EVENT + "'); })"
    //                + ".on('hide', function(){ $(this).trigger('" + SINGULAR_PROCESS_EVENT + "'); });"));
    //        }
    //    }
}
