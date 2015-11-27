package br.net.mirante.singular.form.wicket.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class DateMapper implements ControlsFieldComponentMapper {
    
    private static final Logger LOGGER = Logger.getLogger(DateMapper.class.getName());
    
    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer,BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        @SuppressWarnings("unchecked") TextField<?> comp = new TextField<Date>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), Date.class) {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) (new IConverter<Date>() {
                    @Override
                    public Date convertToObject(String date, Locale locale) throws ConversionException {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(MTipoData.FORMAT);
                            sdf.setLenient(false);
                            return sdf.parse(date);
                        } catch (ParseException e) {
                            String msg = String.format(
                                "Can't parse value '%s' with format '%s'.", 
                                date, MTipoData.FORMAT);
                            LOGGER.log(Level.WARNING,msg , e);
                            throw new ConversionException(e);
                        }
                    }

                    @Override
                    public String convertToString(Date date, Locale locale) {
                        return (new SimpleDateFormat(MTipoData.FORMAT)).format(date);
                    }
                });
            }
        };
        formGroup.appendDatepicker(comp.setLabel(labelModel)
                .setOutputMarkupId(true).add(new InputMaskBehavior(Masks.FULL_DATE)));
        return comp;
    }
}
