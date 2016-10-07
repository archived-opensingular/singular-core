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

package org.opensingular.form.type.util;

import org.opensingular.form.STypeSimple;
import org.opensingular.form.SInfoType;
import org.apache.commons.lang3.StringUtils;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SInfoType(name = "YearMonth", spackage = SPackageUtil.class)
public class STypeYearMonth extends STypeSimple<SIYearMonth, YearMonth> {

    public STypeYearMonth() {
        super(SIYearMonth.class, YearMonth.class);
    }

    protected STypeYearMonth(Class<? extends SIYearMonth> instanceClass) {
        super(instanceClass, YearMonth.class);
    }

    @Override
    public YearMonth convertNotNativeNotString(Object value) {
        if (value instanceof Integer) {
            return converterFromInteger((Integer) value);
        } else if (value instanceof Date) {
            Calendar cal = new GregorianCalendar();
            cal.setTime((Date) value);
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (value instanceof Calendar) {
            Calendar cal = (Calendar) value;
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (value instanceof String){
            return YearMonth.parse((String)value, formatter());
        }
        throw createConversionError(value);
    }

    private static DateTimeFormatter formatter() {
        return new DateTimeFormatterBuilder()
                    .appendPattern("MM/yyyy")
                    .toFormatter();
    }

    private YearMonth converterFromInteger(int value) {
        int ano = value % 10000;
        int mes = value / 10000;
        if (mes < 1 || mes > 12) {
            throw createConversionError(value, YearMonth.class, "Não representa um mês válido (entre 1 e 12)", null);
        }
        return YearMonth.of(ano, mes);
    }

    @Override
    public YearMonth fromString(String value) {
        if (StringUtils.isBlank(value)) {    return null;    }
        return YearMonth.parse((String)value, formatter());
    }

    @Override
    protected String toStringPersistence(YearMonth originalValue) {
        if (originalValue == null) {    return null;    }
        return originalValue.format(formatter());
    }

    @Override
    public String toStringDisplayDefault(YearMonth value) {
        return toStringPersistence(value);
    }
}
