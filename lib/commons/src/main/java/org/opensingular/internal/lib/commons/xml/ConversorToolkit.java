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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ConversorToolkit() {}

    @Nullable
    public static Calendar getCalendar(@Nullable String date) {
        return getCalendar(getDateFromDate(date));
    }

    @Nullable
    public static Calendar getTime(@Nullable String date) {
        return getCalendar(getDateFromTime(date));
    }

    @Nullable
    public static Calendar getCalendar(@Nullable java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar dt = Calendar.getInstance(LOCALE);
        dt.setLenient(false);
        dt.setTime(date);
        return dt;
    }

    @Nonnull
    private static synchronized DateFormat getDateFormat() {
        if (dateFormatMedium__ == null) {
            dateFormatMedium__ = DateFormat.getDateInstance(DateFormat.MEDIUM, LOCALE);
            dateFormatMedium__.setLenient(false);
        }
        return dateFormatMedium__;
    }

    @Nullable
    public static java.util.Date getDateFromDate(@Nullable String date) {
        String dt = StringUtils.trimToNull(date);
        if (dt == null) {
            return null;
        } else if (ConversorDataISO8601.isISO8601(dt)) {
            return ConversorDataISO8601.getDate(dt);
        }

        try {
            byte[] newBytes = dt.getBytes(StandardCharsets.UTF_8);
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
            if (dt.length() > 8) {
                return getDateFormat().parse(new String(newBytes, StandardCharsets.UTF_8));
            } else {
                return getDateFormat("dd/MM/yy").parse(new String(newBytes, StandardCharsets.UTF_8));
            }
        } catch (ParseException e) {
            throw SingularException.rethrow("Data inválida (" + date + "): Erro na posição " + e.getErrorOffset(), e);
        }
    }

    @Nullable
    public static java.util.Date getDateFromTime(@Nullable String time) {
        String tm = StringUtils.trimToNull(time);
        if (tm == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(tm);
        } catch (ParseException e) {
            throw SingularException.rethrow(
                    "Hora inválida (" + time + ")", e);
        }
    }

    public static double getDouble(@Nonnull String value) throws NumberFormatException {
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

    public static int getInt(@Nonnull String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("Valor null");
        }
        try {
            return Integer.parseInt(removeCharacterFromString(value.trim(), '.'));
        } catch (NumberFormatException e) {
            throw SingularException.rethrow("Valor inválido (" + value + ")!", e);
        }
    }

    @Nullable
    public static Integer getInteger(@Nullable Object value) throws NumberFormatException {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            String v = StringUtils.trimToNull(value.toString());
            if (v != null) {
                return getInt(v);
            }
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
    @Nonnull
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

    @Nonnull
    public static synchronized DateFormat getDateFormat(String format) {
        if ((format == null) || "medium".equals(format)) {
            return getDateFormat();
        } else if ("short".equals(format)) {
            if (dateFormatShort__ == null) {
                dateFormatShort__ = DateFormat.getDateInstance(DateFormat.SHORT, LOCALE);
                dateFormatShort__.setLenient(false);
            }
            return dateFormatShort__;
        } else if ("long".equals(format)) {
            if (dateFormatLong__ == null) {
                dateFormatLong__ = DateFormat.getDateInstance(DateFormat.LONG, LOCALE);
                dateFormatLong__.setLenient(false);
            }
            return dateFormatLong__;
        } else if ("full".equals(format)) {
            if (dateFormatFull__ == null) {
                dateFormatFull__ = DateFormat.getDateInstance(DateFormat.FULL, LOCALE);
                dateFormatFull__.setLenient(false);
            }
            return dateFormatFull__;
        } else {
            return new SimpleDateFormat(format, LOCALE);
        }
    }

    @Nonnull
    private static synchronized DateFormat getTimeFormat() {
        if (timeFormat__ == null) {
            timeFormat__ = DateFormat.getTimeInstance(DateFormat.MEDIUM, LOCALE);
            timeFormat__.setLenient(false);
        }
        return timeFormat__;
    }

    /** Gera no formado dd/mm/aaaa-hh:mm:ss */
    @Nullable
    public static String printDateTime(@Nullable java.util.Date date) {
        return date == null ? null : printDate(date) + " " + printHour(date);
    }

    /* Gera no formado dd/mm/aa hh:mm */
    @Nullable
    public static String printDateTimeShortAbbreviated(@Nullable java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
            return printDateShort(date);
        } else {
            return printDateTimeShort(date);
        }
    }

    @Nullable
    public static synchronized String printDateTimeShort(@Nullable java.util.Date date) {
        if (date == null) {
            return null;
        }
        if (dateTimeFormat__ == null) {
            dateTimeFormat__ = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT, LOCALE);
            dateTimeFormat__.setLenient(false);
        }
        return dateTimeFormat__.format(date);
    }

    @Nullable
    public static String printDate(@Nullable java.util.Date date, @Nullable String format) {
        return date == null ? null : getDateFormat(format).format(date);
    }

    @Nullable
    public static String printDate(@Nullable java.util.Date date) {
        return date == null ? null : getDateFormat().format(date);
    }

    @Nullable
    public static String printDateShort(@Nullable java.util.Date date) {
        return date == null ? null : getDateFormat("short").format(date);
    }

    @Nonnull
    public static String printDateNotNull(@Nullable java.util.Date date, @Nullable String format) {
        return date == null ? "" : getDateFormat(format).format(date);
    }

    @Nonnull
    public static String printDateNotNull(@Nullable java.util.Date date) {
        return date == null ? "" : getDateFormat().format(date);
    }

    @Nullable
    public static String printHour(@Nullable java.util.Date date) {
        return date == null ? null : getTimeFormat().format(date);
    }

    @Nullable
    public static String printNumber(@Nullable BigDecimal bigDecimal, int precision) {
        if (bigDecimal == null) {
            return null;
        }
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(LOCALE);
        nf.setParseBigDecimal(true);
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(precision);
        nf.setMaximumFractionDigits(precision);
        return nf.format(bigDecimal);
    }

    @Nonnull
    public static String printNumberNotNull(@Nullable BigDecimal bigDecimal, int precision) {
        return StringUtils.defaultString(printNumber(bigDecimal, precision), "");
    }

    @Nullable
    public static String printNumber(@Nullable Double value) {
        return value == null ? null : getNumberFormat(2).format(value);
    }

    @Nonnull
    public static String printNumberNotNull(@Nullable Double value) {
        return value == null ? "" : getNumberFormat(2).format(value);
    }

    @Nonnull
    public static String printNumber(double value) {
        return getNumberFormat(2).format(value);
    }

    @Nullable
    public static String printNumber(@Nullable Double value, int qtdDecimals) {
        return value == null ? null : printNumber(value.doubleValue(), qtdDecimals);
    }

    @Nonnull
    public static String printNumberNotNull(@Nullable Double value, int qtdDecimals) {
        return value == null ? "" : printNumber(value.doubleValue(), qtdDecimals);
    }

    @Nonnull
    public static String printNumber(double value, int qtdDecimals) {
        return getNumberFormat(qtdDecimals).format(value);
    }

    @Nonnull
    public static String printNumber(double value, int qtdDecimals, boolean printZero) {
        if (!printZero && Double.doubleToRawLongBits(value) == 0) {
            return "";
        }
        return getNumberFormat(qtdDecimals).format(value);
    }

    /**
     * Formats the number with the specified number of decimals places.
     */
    @Nullable
    public static String printNumber(@Nullable Number value, int precision) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return ConversorToolkit.printNumber((BigDecimal) value, precision);
        } else if (value instanceof Double) {
            return ConversorToolkit.printNumber((Double) value, precision);
        } else if (isIntegerOrLong(value)) {
            return ConversorToolkit.printNumber(value.longValue(), precision);
        }
        return ConversorToolkit.printNumber(value.doubleValue(), precision);
    }

    /**
     * Formats the number with the specified number of decimals places. Return empty string if value is null.
     */
    @Nonnull
    public static String printNumberNotNull(@Nullable Number value, int precision) {
        return value == null ? "" : printNumber(value, precision);
    }

    private static boolean isIntegerOrLong(@Nullable Number value) {
        return value instanceof Integer || value instanceof Long;
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

    /**
     * Converts a plaint text to HTML escaping special HTML characters.
     *
     * @param converterURL If true, translates http reference as link.
     */
    @Nullable
    public static String plainTextToHtml(@Nullable String original, boolean converterURL) {
        if (original == null) {
            return null;
        }
        String cs = StringEscapeUtils.escapeHtml4(original);
        if (converterURL) {
            cs = converterURL(cs);
        }
        return cs;
    }

    //@formatter:off
    private static final Pattern PATTERN_URL = Pattern.compile( //NOSONAR
            "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s" +
                    "()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>???]))");
    //@formatter:on

    @Nonnull
    private static String converterURL(@Nonnull String original) {
        Matcher matcher = PATTERN_URL.matcher(original);
        if (matcher.find()) {
            if (matcher.group(1).startsWith("http://")) {
                return matcher.replaceAll("<a href=\"$1\">$1</a>");
            } else {
                return matcher.replaceAll("<a href=\"http://$1\">$1</a>");
            }
        } else {
            return original;
        }
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 08:00.
     */
    @Nonnull
    public static String toHour(int minutes) {
        return toHour(minutes, false);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 8:00.
     */
    @Nullable
    public static String toHour(@Nullable Integer min) {
        return toHour(min, null);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 8:00.
     *
     * @param defaultValue Value to use if minutes is null.
     */
    @Nullable
    public static String toHour(@Nullable Integer min, @Nullable String defaultValue) {
        if (min == null) {
            return defaultValue;
        }
        return toHour((int) min, false);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 8:00.
     *
     * @param defaultValue Value to use if minutes is null.
     */
    @Nullable
    public static String toHour(@Nullable Number min, @Nullable String defaultValue) {
        return toHour(min, defaultValue, false);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 08:00.
     *
     * @param hoursWithTwoDigits if false, 480 returns 8:00. If true, returns 08:00.
     * @param defaultValue       Value to use if minutes is null.
     */
    @Nullable
    public static String toHour(@Nullable Number min, @Nullable String defaultValue, boolean hoursWithTwoDigits) {
        if (min == null) {
            return defaultValue;
        }
        return toHour(round(min, 0).intValue(), hoursWithTwoDigits);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 8:00.
     */
    @Nullable
    public static String toHour(@Nullable Number minutes) {
        return toHour(minutes, null, false);
    }

    /**
     * Converts minutes to a String representing hours in 24h format. For example, 480 becomes 08:00.
     *
     * @param hoursWithTwoDigits if false, 480 returns 8:00. If true, returns 08:00.
     */
    @Nonnull
    public static String toHour(int minutes, boolean hoursWithTwoDigits) {
        int h = minutes / 60;
        int m = minutes % 60;

        StringBuilder buffer = new StringBuilder(10);
        if (minutes < 0) {
            buffer.append('-');
            h = (-minutes) / 60;
            m = (-minutes) % 60;
        }

        if (hoursWithTwoDigits && h < 10) {
            buffer.append('0');
        }
        buffer.append(h).append(':');
        if (m < 10) {
            buffer.append('0');
        }
        buffer.append(m);
        return buffer.toString();
    }

    /**
     * Rounds the number to a number with the specified number of decimals places.
     */
    @Nullable
    public static Number round(@Nullable Number value, int decimals) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
            return value;
        } else if (value instanceof BigDecimal) {
            return round((BigDecimal) value, decimals);
        }
        return round(value.doubleValue(), decimals);
    }

    /**
     * Rounds the number to a number with the specified number of decimals places.
     *
     * @param decimals The number of decimals places of the result. May be negative.
     */
    private static BigDecimal round(BigDecimal value, int decimals) {
        return value.setScale(decimals, BigDecimal.ROUND_HALF_UP);
        //return value.round(new MathContext(decimals));
    }

    /**
     * Rounds the double to the number of decimals places specified.
     *
     * @param decimals The number of decimals places of the result. May be negative.
     */
    @Nullable
    public static Double round(@Nullable Double value, int decimals) {
        return value == null ? null : round(value.doubleValue(), decimals);
    }

    /**
     * Rounds the double to the number of decimals places specified.
     *
     * @param decimals The number of decimals places of the result. May be negative.
     */
    public static double round(double value, int decimals) {
        double p = Math.pow(10, decimals);
        return Math.round(value * p) / p;
    }

    /**
     * Eliminates decimals places of the number.
     *
     * @param decimals The number of decimals places to be keep.
     */
    @Nullable
    public static Double truncate(@Nullable Double value, int decimals) {
        //Uses BigDecimal because sometimes doubles has problemas with "dizimas"
        return value == null ? null : truncateInternal(BigDecimal.valueOf(value), decimals).doubleValue();
    }

    /**
     * Eliminates decimals places of the number.
     *
     * @param decimals The number of decimals places to be keep.
     */
    @Nullable
    public static BigDecimal truncate(@Nullable BigDecimal value, int decimals) {
        return value == null ? null : truncateInternal(value, decimals);
    }

    @Nonnull
    private static BigDecimal truncateInternal(@Nonnull BigDecimal value, int decimals) {
        return value.setScale(decimals, RoundingMode.DOWN);
    }

    /**
     * Add both number. If both number are null ,returns null.
     * <p>Tries to preserve the original type of the numbers, i.e., when multiplying two Integer, the result will be a
     * Integer, but if the numbers aren't the same class, both are converted to BigDecimal.</p>
     */
    @Nullable
    public static Number add(@Nullable Number a, @Nullable Number b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else if (a.getClass() == b.getClass()) {
            if (a instanceof BigDecimal) {
                return ((BigDecimal) a).add((BigDecimal) b);
            } else if (a instanceof Integer) {
                return a.intValue() + b.intValue();
            } else if (a instanceof Long) {
                return a.longValue() + b.longValue();
            } else if (a instanceof BigInteger) {
                return ((BigInteger) a).add((BigInteger) b);
            }
        }
        return toBigDecimal(a).add(toBigDecimal(b), MathContext.DECIMAL32);
    }

    /**
     * Multiplies both number if they are not null. If any of the numbers is null, the result is null.
     * <p>Tries to preserve the original type of the numbers, i.e., when multiplying two Integer, the result will be a
     * Integer, but if the numbers aren't the same class, both are converted to BigDecimal.</p>
     */
    @Nullable
    public static Number multiply(@Nullable Number a, @Nullable Number b) {
        if (a == null || b == null) {
            return null;
        } else if (a.getClass() == b.getClass()) {
            if (a instanceof BigDecimal) {
                return ((BigDecimal) a).multiply((BigDecimal) b);
            } else if (a instanceof Integer) {
                return a.intValue() * b.intValue();
            } else if (a instanceof Long) {
                return a.longValue() * b.longValue();
            } else if (a instanceof BigInteger) {
                return ((BigInteger) a).multiply((BigInteger) b);
            }
        }
        return toBigDecimal(a).multiply(toBigDecimal(b), MathContext.DECIMAL32);
    }

    /**
     * Divides the numbers converting both do BigDecimal first. If any number is null, the result is null. Division by
     * zero returns null.
     */
    @Nullable
    public static BigDecimal divide(@Nullable Number a, @Nullable Number b) {
        if (a == null || isZeroOrNull(b)) {
            return null;
        }
        return toBigDecimal(a).divide(toBigDecimal(b), MathContext.DECIMAL32);
    }

    /** Returns true if the number is zero. Null value return false. */
    public static boolean isZero(@Nullable Number a) {
        return a != null && isZeroInternal(a);
    }

    /** Returns true if the number is zero or null. */
    public static boolean isZeroOrNull(@Nullable Number a) {
        return a == null || isZeroInternal(a);
    }

    /** Returns true if the number is zero. */
    private static boolean isZeroInternal(@Nonnull Number a) {
        if (a instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo(BigDecimal.ZERO) == 0;
        } else if (a instanceof Integer || a instanceof Long) {
            return a.longValue() == 0;
        }
        return Double.doubleToRawLongBits(a.doubleValue()) == 0;
    }

    /**
     * Converts a Number to BigDecimal.
     */
    @Nullable
    public static BigDecimal toBigDecimal(@Nullable Number a) {
        if (a == null) {
            return null;
        } else if (a instanceof BigDecimal) {
            return (BigDecimal) a;
        } else if (a instanceof Double || a instanceof Float) {
            return BigDecimal.valueOf(a.doubleValue());
        } else if (a instanceof Integer) {
            return BigDecimal.valueOf(a.intValue());
        } else if (a instanceof BigInteger) {
            return new BigDecimal((BigInteger) a);
        } else {
            return BigDecimal.valueOf(a.longValue());
        }
    }
}
