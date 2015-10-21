package br.net.mirante.singular.form.wicket.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

import static br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;

public class DateMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        @SuppressWarnings("unchecked") TextField<?> comp = new TextField<Date>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), Date.class) {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) (new IConverter<Date>() {
                    @Override
                    public Date convertToObject(String date, Locale locale) throws ConversionException {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                            sdf.setLenient(false);
                            return sdf.parse(date);
                        } catch (ParseException e) {
                            throw new ConversionException(e);
                        }
                    }

                    @Override
                    public String convertToString(Date date, Locale locale) {
                        return (new SimpleDateFormat("ddMMyyyy")).format(date);
                    }
                });
            }
        };
        formGroup.appendDatepicker(comp.setLabel(labelModel)
                .setOutputMarkupId(true).add(new InputMaskBehavior(Masks.FULL_DATE)));
        return comp;
    }
}
