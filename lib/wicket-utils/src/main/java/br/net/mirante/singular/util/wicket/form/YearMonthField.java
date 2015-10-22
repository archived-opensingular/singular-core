package br.net.mirante.singular.util.wicket.form;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public final class YearMonthField extends TextField<YearMonth> {

    public YearMonthField(String id, IModel<YearMonth> model) {
        super(id, model, YearMonth.class);
    }

    @SuppressWarnings("unchecked")
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new IConverter<YearMonth>() {
            @Override
            public YearMonth convertToObject(String value, Locale locale) throws ConversionException {
                try {
                    return YearMonth.parse(value, DateTimeFormatter.ofPattern("MMyyyy"));
                } catch (DateTimeParseException ex) {
                    throw new ConversionException(ex);
                }
            }

            @Override
            public String convertToString(YearMonth value, Locale locale) {
                return value.toString();
            }
        };
    }
}
