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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Fornece métodos de conversão de string de Data/Hora no formato
 * ISO8601 para objeto Java (Date, Timestamp, Calendar, etc.). O
 * formato ISO8601 é utilizado para manter compatibildiade com a formatação
 * de tipos do XML Schema (ver http://www.w3.org).<p>
 * <p>
 * O formato ISO8601 é indenpendente de Locale. Um exemplo é<br>
 * 1999-05-31T13:20:00.000-05:00<p>
 * <p>
 * Para maiores informações sobre o formato veja
 * <a href="http://www.w3.org/TR/xmlschema-0/">http://www.w3.org/TR/xmlschema-0/
 * </a>.
 *
 * @author Daniel C. Bordin
 */
public final class ConversorDataISO8601 {

    /**
     * Separador entre a data e as informações de hora
     */
    private static final char DATE_TIME_SEPARATOR = 'T';

    private static final byte YEAR = 1;
    private static final byte MONTH = 2;
    private static final byte DAY = 3;
    private static final byte HOUR = 4;
    private static final byte MINUTE = 5;
    private static final byte SECONDS = 6;
    private static final byte MILLI = 7;
    private static final byte NANO    = 8;

    private ConversorDataISO8601() {
    }

    @Nonnull
    public static String format(@Nonnull java.util.Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        return format(gc);
    }

    @Nonnull
    public static java.util.Date getDate(@Nonnull String s) {
        return getCalendar(s).getTime();
    }

    @Nonnull
    public static String format(@Nonnull Calendar gc) {
        byte precision = SECONDS;
        if (gc.get(Calendar.MILLISECOND) != 0) {
            precision = MILLI;
        }
        return format(
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH) + 1,
                gc.get(Calendar.DAY_OF_MONTH),
                gc.get(Calendar.HOUR_OF_DAY),
                gc.get(Calendar.MINUTE),
                gc.get(Calendar.SECOND),
                gc.get(Calendar.MILLISECOND),
                0, precision);
    }

    @Nonnull
    public static GregorianCalendar getCalendar(@Nonnull String s) {
        int[] t = valueOf(s);
        GregorianCalendar gc =
                new GregorianCalendar(t[YEAR], t[MONTH] - 1, t[DAY], t[HOUR], t[MINUTE], t[SECONDS]);
        if (t[NANO] != 0) {
            gc.set(Calendar.MILLISECOND, t[NANO] / 1000000);
        }
        return gc;
    }

    /**
     * Classe de apoio para o parse da Data no formato ISO8601
     */
    private static class StringReader {
        private final String text;
        private       int pos;

        public StringReader(String text) {
            this.text = text;
        }

        boolean isNotEnd() {
            return pos != text.length();
        }

        public int readNumber(int minimumDigits, int maximumDigits, boolean maxShift) {
            int  p = 0;
            int  n = 0;
            char c;
            while (pos < text.length()) {
                c = text.charAt(pos);
                if (Character.isDigit(c)) {
                    if (p == 0) {
                        n = (c - '0');
                    } else {
                        n = n * 10 + (c - '0');
                    }
                } else {
                    if (p == 0) {
                        throw errorFormat();
                    } else {
                        break;
                    }
                }
                pos++;
                p++;
            }
            validate(minimumDigits, maximumDigits, p);
            n = letShiftMaximo(maximumDigits, maxShift, p, n);
            return n;
        }

        protected void validate(int minimumDigits, int maximumDigits, int p) {
            if ((p < minimumDigits) || (p > maximumDigits)) {
                throw errorFormat();
            }
        }

        int letShiftMaximo(int maximumDigits, boolean maxShift, int p, int n) {
            int _p = p;
            int _n = n;
            if (maxShift) {
                for (; _p < maximumDigits; _p++) {
                    _n *= 10;
                }
            }
            return _n;
        }

        void readDateSeparator() {
            char c = readCharacter();
            if (!((c == '-') || (c == '.') || (c == '/'))) {
                throw errorFormat();
            }
        }

        void readDateTimeSeparator() {
            char c = readCharacter();
            if (!((c == DATE_TIME_SEPARATOR) || (c == ' '))) {
                throw errorFormat();
            }
        }

        boolean hasChar(char c) {
            if ((pos != text.length()) && (c == text.charAt(pos))) {
                pos++;
                return true;
            }
            return false;
        }

        void readCharacter(char c) {
            if (c != readCharacter()) {
                throw errorFormat();
            }
        }

        char readCharacter() {
            if (pos == text.length()) {
                throw errorFormat();
            }
            return text.charAt(pos++);
        }

        RuntimeException errorFormat() {
            throw new IllegalArgumentException(
                    "A string '" + text + "' deveria estar no formato yyyy-mm-dd hh:mm:ss.fffffffff");
        }

    }

    @Nonnull
    private static int[] valueOf(@Nonnull String s) {
        StringReader reader = new StringReader(Objects.requireNonNull(s));

        int[] t = new int[NANO + 1];

        t[YEAR] = reader.readNumber(4, 10, false);
        reader.readDateSeparator();
        t[MONTH] = reader.readNumber(1, 2, false);
        reader.readDateSeparator();
        t[DAY] = reader.readNumber(1, 2, false);

        // hour optional
        if (reader.isNotEnd()) {
            reader.readDateTimeSeparator();
            t[HOUR] = reader.readNumber(1, 2, false);
            reader.readCharacter(':');
            t[MINUTE] = reader.readNumber(1, 2, false);
            //segundos opcionais
            if (reader.isNotEnd()) {
                reader.readCharacter(':');
                t[SECONDS] = reader.readNumber(1, 2, false);
                // nanos/milis opcionais
                if (reader.hasChar('.')) {
                    t[NANO] = reader.readNumber(1, 9, true);
                }
            }
            //indicador diferença GMT em miliseconds
            //if (leitor.hasChar('-')) {
            //   int hGMT = leitor.readNumber(1, 2, false);
            //    leitor.readCharacter(':');
            //    int mGMT = leitor.readNumber(1, 2, false);
            //    gmtMili = (hGMT * 60 + mGMT) * 60 * 1000;
            //}
        }

        if (reader.isNotEnd() && !reader.hasChar('+') && !reader.hasChar('-')) {
            throw reader.errorFormat();
        }

        return t;
    }

    @Nonnull
    private static String format(
            int year,
            int month,
            int day,
            int hour,
            int minute,
            int second, int milli,
            int nano,
            byte precision) {

        StringBuilder buffer = new StringBuilder(40);

        formatYearMonthDay(buffer, year, month, day);

        if ((precision == DAY) || isTimeZero(hour, minute, second, milli, nano)) {
            return buffer.toString();
        }

        buffer.append(DATE_TIME_SEPARATOR);
        format2(buffer, hour);
        buffer.append(':');
        format2(buffer, minute);
        buffer.append(':');
        format2(buffer, second);

        if (nano == 0) {
            formatMilliIfNecessary(buffer, milli, precision);
        } else if (milli != 0) {
            throw new IllegalArgumentException("Não se pode para mili e nanosegundos");
        } else {
            formatMilliAndNanoIfNecessary(buffer, nano, precision);
        }

        return buffer.toString();
    }

    private static boolean isTimeZero(int hour, int minute, int second, int milli, int nano) {
        if ((hour == 0) && (minute == 0) && (second == 0)) {
            return (milli == 0) && (nano == 0);
        }
        return false;
    }

    private static void formatYearMonthDay(@Nonnull StringBuilder buffer, int year, int month, int day) {
        if (year < 0) {
            throw new IllegalArgumentException("Ano Negativo");
        } else if (year < 10) {
            buffer.append("000");
        } else if (year < 100) {
            buffer.append("00");
        } else if (year < 1000) {
            buffer.append('0');
        }
        buffer.append(year);
        buffer.append('-');
        format2(buffer, month);
        buffer.append('-');
        format2(buffer, day);
    }

    private static void formatMilliAndNanoIfNecessary(@Nonnull StringBuilder buffer, int nano, byte precision) {
        int milli;
        if ((nano < 0) || (nano > 999999999)) {
            throw new IllegalArgumentException("Nanos <0 ou >999999999");
        }
        // Geralmente so tem precisão de mili segundos
        // Se forem apenas milisegundos fica .999
        // Se realm
        milli = nano / 1000000;
        formatMilliIfNecessary(buffer, milli, precision);
        if (precision == NANO) {
            int onlyNano = nano % 1000000;
            if (onlyNano != 0) {
                String nanoS = Integer.toString(onlyNano);
                for (int i = 6 - nanoS.length(); i != 0; i--) {
                    buffer.append('0');
                }
                //Trunca zeros restantes
                int ultimo = nanoS.length() - 1;
                while ((ultimo != -1) && (nanoS.charAt(ultimo) == '0')) {
                    ultimo--;
                }
                for (int i = 0; i <= ultimo; i++) {
                    buffer.append(nanoS.charAt(i));
                }
            }
        }
    }

    private static void format2(@Nonnull StringBuilder buffer, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("valor negativo");
        } else if (value < 10) {
            buffer.append('0');
        } else if (value > 99) {
            throw new IllegalArgumentException("valor > 99");
        }
        buffer.append(value);
    }

    private static void formatMilliIfNecessary(@Nonnull StringBuilder buffer, int milli, byte precision) {
        if (milli < 0) {
            throw new IllegalArgumentException("Milisegundos <0");
        } else if (milli > 999) {
            throw new IllegalArgumentException("Milisegundos >999");
        }
        if ((precision == MILLI) || (precision == NANO)) {
            buffer.append('.');
            if (milli < 10) {
                buffer.append("00");
            } else if (milli < 100) {
                buffer.append('0');
            }
            buffer.append(milli);
        }
    }

    /**
     * Verifica se a string fornecida esta no formato ISO8601.
     *
     * @param value a ser verificado
     * @return true se atender ao formato
     */
    public static boolean isISO8601(@Nullable String value) {
        //                01234567890123456789012345678
        //                1999-05-31T13:20:00.000-05:00
        String mask1 = "????-??-??T??:??:??.???-??:??";
        String mask2 = "????-??-?? ??:??:??.???+??:??";
        if ((value == null) || value.length() < 10 || value.length() > mask1.length()) {
            return false;
        }
        int tam = value.length();
        for (int i = 0; i < tam; i++) {
            char m = mask1.charAt(i);
            if (m == '?') {
                if (!Character.isDigit(value.charAt(i))) {
                    return false;
                }
            } else if (m != value.charAt(i) && mask2.charAt(i) != value.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
