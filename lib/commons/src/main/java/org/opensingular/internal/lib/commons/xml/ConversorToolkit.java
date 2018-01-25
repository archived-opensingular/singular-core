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

package org.opensingular.internal.lib.commons.xml;

import org.opensingular.lib.commons.base.SingularException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Expedito Júnior
 */
public final class ConversorToolkit {

    public static final Locale LOCALE = new Locale("pt", "br");

    private static DateFormat dateFormatShort__;
    private static DateFormat dateFormatMedium__;
    private static DateFormat dateFormatLong__;
    private static DateFormat dateFormatFull__;

    private static DateFormat timeFormat__;

    private static DateFormat dateTimeFormat__;

    private static final NumberFormat[] numberFormat__ = new NumberFormat[30];

    /**
     * Esconde o contrutor
     */
    private ConversorToolkit() {
        super();
    }

    private static void verifyNull(Object value) throws ParseException {
        if (value == null) {
            throw new ParseException("Valor nulo", 0);
        }
    }

    public static Calendar getCalendar(String data) {
        return getCalendar(getDateFromData(data));
    }

    public static Calendar getTime(String data) {
        return getCalendar(getTimeFromData(data));
    }

    public static Calendar getCalendar(java.util.Date data) {
        Calendar dt = Calendar.getInstance(LOCALE);
        dt.setLenient(false);
        dt.setTime(data);
        return dt;
    }

    private static synchronized DateFormat getDateFormat() {
        if (dateFormatMedium__ == null) {
            dateFormatMedium__ = DateFormat.getDateInstance(DateFormat.MEDIUM, LOCALE);
            dateFormatMedium__.setLenient(false);
        }
        return dateFormatMedium__;
    }

    public static java.util.Date getDateFromData(String data) {
        if (ConversorDataISO8601.isISO8601(data)) {
            return ConversorDataISO8601.getDate(data);
        }

        try {
            verifyNull(data);
            byte[] newBytes = data.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < newBytes.length; i++) {
                switch (newBytes[i]) {
                    case '\\':
                    case '-':
                    case '.':
                    case ' ':
                        newBytes[i] = (byte) '/';
                        break;
                    default:
                }
            }
            if (data.length() > 8) {
                return getDateFormat().parse(new String(newBytes, StandardCharsets.UTF_8));
            } else {
                return getDateFormat("dd/MM/yy").parse(new String(newBytes, StandardCharsets.UTF_8));
            }
        } catch (ParseException e) {
            throw SingularException.rethrow(
                    "Data inválida (" + data + "): Erro na posição " + e.getErrorOffset(), e);
        }
    }

    public static java.util.Date getTimeFromData(String time) {
        try {
            verifyNull(time);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            return sdf.parse(time);
        } catch (ParseException e) {
            throw SingularException.rethrow(
                    "Hora inválida (" + time + ")", e);
        }
    }

    public static double getDouble(String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("Valor null");
        }
        if ("-".equals(value)) {
            return 0;
        }
        try {
            String v = value;
            if (v.contains(",")) {
                v = removeCharacterFromString(v.trim(), '.');
            }
            return Double.parseDouble(v.replace(',', '.'));
        } catch (Exception e) {
            throw SingularException.rethrow("Valor inválido (" + value + ")!", e);
        }
    }

    public static int getInt(String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("Valor null");
        }
        try {
            return Integer.parseInt(removeCharacterFromString(value.trim(), '.'));
        } catch (NumberFormatException e) {
            throw SingularException.rethrow("Valor inválido (" + value + ")!", e);
        }
    }

    public static Integer getInt(Object value) throws NumberFormatException {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            return getInt(value.toString());
        }
        return null;
    }

    /**
     * Retorna o formatado que ira gera a quantidade especificadas de casas
     * decimais.
     *
     * @param decimals Se for -1, então o formatador não força a qtd de decimais
     * @return sempre difente de null.
     */
    private static NumberFormat getNumberFormat(int decimals) {
        if (decimals == -1) {
            return NumberFormat.getInstance(LOCALE);
        }
        if (numberFormat__[decimals] == null) {
            NumberFormat nf = NumberFormat.getInstance(LOCALE);
            nf.setMaximumFractionDigits(decimals);
            nf.setMinimumFractionDigits(decimals);
            numberFormat__[decimals] = nf;
        }
        return numberFormat__[decimals];
    }

    public static synchronized DateFormat getDateFormat(String formato) {
        if ((formato == null) || "medium".equals(formato)) {
            return getDateFormat();
        } else if ("short".equals(formato)) {
            if (dateFormatShort__ == null) {
                dateFormatShort__ = DateFormat.getDateInstance(DateFormat.SHORT, LOCALE);
                dateFormatShort__.setLenient(false);
            }
            return dateFormatShort__;
        } else if ("long".equals(formato)) {
            if (dateFormatLong__ == null) {
                dateFormatLong__ = DateFormat.getDateInstance(DateFormat.LONG, LOCALE);
                dateFormatLong__.setLenient(false);
            }
            return dateFormatLong__;
        } else if ("full".equals(formato)) {
            if (dateFormatFull__ == null) {
                dateFormatFull__ = DateFormat.getDateInstance(DateFormat.FULL, LOCALE);
                dateFormatFull__.setLenient(false);
            }
            return dateFormatFull__;
        } else {
            return new SimpleDateFormat(formato, LOCALE);
        }
    }

    private static synchronized DateFormat getTimeFormat() {
        if (timeFormat__ == null) {
            timeFormat__ = DateFormat.getTimeInstance(DateFormat.MEDIUM, LOCALE);
            timeFormat__.setLenient(false);
        }
        return timeFormat__;
    }

    /** Gera no formado dd/mm/aaaa-hh:mm:ss */
    public static String printDateTime(java.util.Date data) {
        return printDate(data) + " " + printHour(data);
    }

    /* Gera no formado dd/mm/aa hh:mm */
    public static String printDateTimeShortAbbreviated(java.util.Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
            return printDateShort(data);
        } else {
            return printDateTimeShort(data);
        }
    }

    public static synchronized String printDateTimeShort(java.util.Date data) {
        if (data == null) {
            return null;
        }
        if (dateTimeFormat__ == null) {
            dateTimeFormat__ = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT, LOCALE);
            dateTimeFormat__.setLenient(false);
        }
        return dateTimeFormat__.format(data);
    }

    public static String printDate(java.util.Date data, String formato) {
        return getDateFormat(formato).format(data);
    }

    public static String printDate(java.util.Date data) {
        if (data == null) {
            return null;
        }
        return getDateFormat().format(data);
    }

    public static String printDateShort(java.util.Date data) {
        if (data == null) {
            return null;
        }
        return getDateFormat("short").format(data);
    }

    public static String printDateNotNull(java.util.Date data, String formato) {
        if (data == null) {
            return "";
        }
        return getDateFormat(formato).format(data);
    }

    public static String printDateNotNull(java.util.Date data) {
        if (data == null) {
            return "";
        }
        return getDateFormat().format(data);
    }

    public static String printHour(java.util.Date data) {
        return getTimeFormat().format(data);
    }

    public static String printNumber(BigDecimal bigDecimal, Integer precision) {
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(LOCALE);
        nf.setParseBigDecimal(true);
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(precision);
        nf.setMaximumFractionDigits(precision);
        return nf.format(bigDecimal);
    }
    
    public static String printNumber(Double value) {
        if (value == null) {
            return null;
        }
        return getNumberFormat(2).format(value);
    }

    public static String printNumber(double value) {
        return getNumberFormat(2).format(value);
    }

    public static String printNumber(Double value, int qtdDecimals) {
        if (value == null) {
            return "";
        }
        return printNumber(value.doubleValue(), qtdDecimals);
    }

    public static String printNumber(double value, int qtdDecimals) {
        return getNumberFormat(qtdDecimals).format(value);
    }

    public static String printNumber(double value, int qtdDecimals, boolean printZero) {
        if (!printZero && Double.doubleToRawLongBits(value) == 0) {
            return "";
        }
        return getNumberFormat(qtdDecimals).format(value);
    }

    public static String breakHtmlLines(String text) {
        return text.replace("\n", "<br/>");
    }

    private static String removeCharacterFromString(String value, char targetChar) {
        String tempValue = value;
        for (int i = tempValue.indexOf(targetChar); i != -1; i = tempValue.indexOf(targetChar, i)) {
            tempValue = tempValue.substring(0, i) + tempValue.substring(i + 1);
        }
        return tempValue;
    }
}
