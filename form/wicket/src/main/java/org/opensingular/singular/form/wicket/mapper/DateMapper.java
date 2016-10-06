/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper;

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

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.wicket.IAjaxUpdateListener;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.singular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.singular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.util.wicket.bootstrap.datepicker.BSDatepickerInputGroup;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class DateMapper extends AbstractControlsFieldComponentMapper {

    private static final Logger LOGGER = Logger.getLogger(DateMapper.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        BSDatepickerInputGroup datepicker = formGroup
            .newComponent(id -> (BSDatepickerInputGroup) new BSDatepickerInputGroup(id)
                .setConverter(new ConverterImpl())
                .setTextFieldConfigurer((FormComponent<?> c) -> c
                    .setLabel(labelModel)
                    .setDefaultModel(new SInstanceValueModel<>(model))
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

    @SuppressWarnings("rawtypes")
    private static final class ConverterImpl implements IConverter {
        @Override
        public Object convertToObject(String date, Locale locale) throws ConversionException {
            if ("//".equals(date))
                return null;
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
