package br.net.mirante.singular.form.wicket.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.datepicker.BSDatepickerConstants;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.jquery.JQuery;

@SuppressWarnings("serial")
public class DateMapper implements ControlsFieldComponentMapper {

    private static final Logger LOGGER = Logger.getLogger(DateMapper.class.getName());

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends SInstance> model, IModel<String> labelModel) {
        @SuppressWarnings("unchecked")
        TextField<?> comp = new TextField<Date>(model.getObject().getNome(),
            new MInstanciaValorModel<>(model), Date.class) {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) (new IConverter<Date>() {
                    @Override
                    public Date convertToObject(String date, Locale locale) throws ConversionException {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(STypeData.FORMAT);
                            sdf.setLenient(false);
                            return sdf.parse(date);
                        } catch (ParseException e) {
                            String msg = String.format(
                                "Can't parse value '%s' with format '%s'.",
                                date, STypeData.FORMAT);
                            LOGGER.log(Level.WARNING, msg, e);
                            throw new ConversionException(e);
                        }
                    }

                    @Override
                    public String convertToString(Date date, Locale locale) {
                        return (new SimpleDateFormat(STypeData.FORMAT)).format(date);
                    }
                });
            }
        };
        formGroup.appendDatepicker(comp.setLabel(labelModel)
            .setOutputMarkupId(true)
            .add(new InputMaskBehavior(Masks.FULL_DATE)));
        return comp;
    }

    @Override
    public void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.add(new BSDatepickerAjaxUpdateBehavior(model, listener));
    }

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        if ((model != null) && (model.getObject() != null)) {
            SInstance instancia = model.getObject();
            if (instancia.getValor() instanceof Date) {
                Date dt = (Date) instancia.getValor();
                final SimpleDateFormat formattter = new SimpleDateFormat(STypeData.FORMAT);
                return formattter.format(dt);
            }
        }
        return StringUtils.EMPTY;
    }

    private static final class BSDatepickerAjaxUpdateBehavior extends AjaxUpdateInputBehavior {

        private transient boolean flag;

        private BSDatepickerAjaxUpdateBehavior(IModel<SInstance> model, IAjaxUpdateListener listener) {
            super(BSDatepickerConstants.JS_CHANGE_EVENT, model, listener);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            if (flag)
                attributes.setEventNames();
        }

        @Override
        protected CharSequence getCallbackScript(Component component) {
            flag = true;
            try {
                return JQuery.on(component, super.getEvent(), super.getCallbackScript(component));
            } finally {
                flag = false;
            }
        }
    }
}
