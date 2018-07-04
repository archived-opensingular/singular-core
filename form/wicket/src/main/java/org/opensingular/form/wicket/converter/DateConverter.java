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

package org.opensingular.form.wicket.converter;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.opensingular.lib.commons.util.Loggable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter implements IConverter<Date>, Loggable {

    private SimpleDateFormat simpleDateFormat;

    public DateConverter(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public Date convertToObject(String date, Locale locale) throws ConversionException {
        if ("//".equals(date))
            return null;
        try {
            simpleDateFormat.setLenient(false);
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            String msg = String.format(
                    "Can't parse value '%s' with format '%s'.",
                    date, "dd/MM/yyyy");
            getLogger().warn(msg, e);
            throw new ConversionException(e);
        }
    }

    @Override
    public String convertToString(Date date, Locale locale) {
        return simpleDateFormat.format(date);
    }
}
