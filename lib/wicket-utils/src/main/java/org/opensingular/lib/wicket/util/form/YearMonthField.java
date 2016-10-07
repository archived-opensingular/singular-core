/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.form;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

@SuppressWarnings("serial")
public final class YearMonthField extends TextField<YearMonth> {
    private static final Logger LOGGER = Logger.getLogger(YearMonthField.class.getName());
    private static final String FORMAT = "MM/yyyy";

    public YearMonthField(String id, IModel<YearMonth> model) {
        super(id, model, YearMonth.class);
    }

    @SuppressWarnings("unchecked")
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new IConverter<YearMonth>() {
            @Override
            public YearMonth convertToObject(String value, Locale locale) throws ConversionException {
                try {
                    return YearMonth.parse(value, DateTimeFormatter.ofPattern(FORMAT));
                } catch (DateTimeParseException ex) {
                    String msg = String.format(
                        "Can't parse value '%s' with format '%s'.", 
                        value, FORMAT);
                    LOGGER.log(Level.WARNING,msg , ex);
                    throw new ConversionException(ex);
                }
            }

            @Override
            public String convertToString(YearMonth value, Locale locale) {
                return String.format("%02d%04d", value.getMonthValue(), value.getYear());
            }
        };
    }
}
